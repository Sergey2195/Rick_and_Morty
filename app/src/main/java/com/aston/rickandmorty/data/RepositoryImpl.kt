package com.aston.rickandmorty.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aston.rickandmorty.data.apiCalls.ApiCall
import com.aston.rickandmorty.data.localDataSource.LocalRepository
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.AllLocationsResponse
import com.aston.rickandmorty.data.pagingSources.CharactersPagingSource
import com.aston.rickandmorty.data.pagingSources.EpisodesPagingSource
import com.aston.rickandmorty.data.pagingSources.LocationsPagingSource
import com.aston.rickandmorty.data.remoteDataSource.RemoteRepository
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.entity.*
import com.aston.rickandmorty.domain.repository.Repository
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.utils.Utils
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@ApplicationScope
class RepositoryImpl @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val apiCall: ApiCall
) : Repository {

    private val loadingProgressStateFlow = MutableStateFlow(false)

    override fun getLoadingProgressStateFlow(): StateFlow<Boolean> {
        return loadingProgressStateFlow.asStateFlow()
    }

    override suspend fun invalidateCharactersData() {
        localRepository.deleteAllCharactersData()
    }

    private fun setLoading(isLoading: Boolean) {
        loadingProgressStateFlow.value = isLoading
    }

    override fun getFlowAllCharacters(
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): Flow<PagingData<CharacterModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = {
                CharactersPagingSource { pageIndex ->
                    loadCharacters(
                        pageIndex,
                        nameFilter,
                        statusFilter,
                        speciesFilter,
                        typeFilter,
                        genderFilter
                    )
                }
            }
        ).flow
    }

    override fun getFlowAllLocations(
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?
    ): Flow<PagingData<LocationModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = {
                LocationsPagingSource { pageIndex ->
                    loadLocations(pageIndex, nameFilter, typeFilter, dimensionFilter)
                }
            }
        ).flow
    }

    private suspend fun loadCharacters(
        pageIndex: Int,
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): AllCharactersResponse {
        setLoading(true)
        val localResponse = localRepository.getAllCharacters(
            pageIndex, nameFilter, statusFilter, speciesFilter, typeFilter, genderFilter
        )
        val resultResponse = if (localResponse.listCharactersInfo == null) {
            remoteRepository.getAllCharacters(
                pageIndex,
                nameFilter,
                statusFilter,
                speciesFilter,
                typeFilter,
                genderFilter
            ).also { localRepository.writeResponse(it) }
        } else {
            localResponse
        }
        setLoading(false)
        return resultResponse
    }

    private suspend fun loadLocations(
        pageIndex: Int,
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?
    ): AllLocationsResponse {
        setLoading(true)
        val localResponse =
            localRepository.getAllLocations(pageIndex, nameFilter, typeFilter, dimensionFilter)
        val resultResponse = if (localResponse.listLocationsInfo == null) {
            apiCall.getAllLocations(pageIndex, nameFilter, typeFilter, dimensionFilter).also {
                localRepository.writeResponseLocation(it)
            }
        } else {
            localResponse
        }
        setLoading(false)
        return resultResponse
    }

    override suspend fun getSingleCharacterData(id: Int): CharacterDetailsModel? {
        return try {
            val localResult = localRepository.getSingleCharacterInfo(id)
            if (localResult == null) {
                val remoteResult = remoteRepository.getSingleCharacterInfo(id)
                localRepository.writeSingleCharacterInfo(remoteResult)
                return Mapper.transformCharacterInfoRemoteIntoCharacterDetailsModel(remoteResult)
            }
            return localResult
        } catch (e: java.lang.Exception) {
            null
        }
    }

    override fun getSingleLocationData(id: Int): Single<LocationDetailsModelWithId> {
        return apiCall.getSingleLocationData(id)
            .map {Mapper.transformLocationInfoRemoteInfoLocationDetailsModelWithIds(it)}
    }

    override fun getFlowAllEpisodes(
        nameFilter: String?,
        episodeFilter: String?
    ): Flow<PagingData<EpisodeModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = {
                EpisodesPagingSource {
                    apiCall.getAllEpisodes(it, nameFilter, episodeFilter)
                }
            }
        ).flow
    }

    override suspend fun getSingleEpisodeData(id: Int): EpisodeDetailsModel? {
        return try {
            val result = apiCall.getSingleEpisodeData(id)
            val listId =
                result.episodeCharacters?.map { Utils.getLastIntAfterSlash(it) } ?: emptyList()
            val requestStr = Utils.getStringForMultiId(listId)
            var listCharactersModel = emptyList<CharacterModel>()
            if (requestStr.isNotBlank()) {
                val characters = apiCall.getMultiCharactersData(requestStr)
                listCharactersModel =
                    Mapper.transformListCharacterInfoRemoteIntoCharacterModel(characters)
            }
            return Mapper.transformEpisodeInfoRemoteIntoEpisodeDetailsModel(
                result,
                listCharactersModel
            )
        } catch (e: java.lang.Exception) {
            null
        }
    }

    override suspend fun getLocationModel(id: Int): LocationModel? {
        return try {
            val response = apiCall.getSingleLocationDataCoroutine(id)
            Mapper.transformLocationInfoRemoteIntoLocationModel(response)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getListEpisodeModel(multiId: String): List<EpisodeModel>? {
        return try {
            val list = if (multiId.contains(',')) {
                apiCall.getMultiEpisodesData(multiId)
            } else {
                listOf(apiCall.getSingleEpisodeData(multiId.toInt()))
            }
            list?.map { Mapper.transformEpisodeInfoRemoteIntoEpisodeModel(it) }
        } catch (e: Exception) {
            null
        }
    }

    override fun getCountOfCharacters(
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): Single<Int> {
        return apiCall.getCountOfCharacters(
            nameFilter,
            statusFilter,
            speciesFilter,
            typeFilter,
            genderFilter
        )
            .map { it.pageInfo?.countOfElements }
    }

    override fun getCountOfLocations(
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?
    ): Single<Int> {
        return apiCall.getCountOfLocations(nameFilter, typeFilter, dimensionFilter)
            .map { it.pageInfo?.countOfElements }
    }

    override fun getCountOfEpisodes(nameFilter: String?, episodeFilter: String?): Single<Int> {
        return apiCall.getCountOfEpisodes(nameFilter, episodeFilter)
            .map { it.pageInfo?.countOfElements }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}