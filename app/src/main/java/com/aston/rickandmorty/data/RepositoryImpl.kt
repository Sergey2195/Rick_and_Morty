package com.aston.rickandmorty.data

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aston.rickandmorty.data.apiCalls.ApiCall
import com.aston.rickandmorty.data.localDataSource.LocalRepository
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.AllEpisodesResponse
import com.aston.rickandmorty.data.models.AllLocationsResponse
import com.aston.rickandmorty.data.models.EpisodeInfoRemote
import com.aston.rickandmorty.data.pagingSources.CharactersPagingSource
import com.aston.rickandmorty.data.pagingSources.EpisodesPagingSource
import com.aston.rickandmorty.data.pagingSources.LocationsPagingSource
import com.aston.rickandmorty.data.remoteDataSource.RemoteRepository
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.entity.*
import com.aston.rickandmorty.domain.repository.Repository
import com.aston.rickandmorty.mappers.Mapper
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ApplicationScope
class RepositoryImpl @Inject constructor(
    application: Application,
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val apiCall: ApiCall
) : Repository {

    private val loadingProgressStateFlow = MutableStateFlow(false)
    private var invalidData = false
    private val connectivityObserver = NetworkConnectivityObserver(application)
    private val connectionStatusIsAvailable =
        MutableStateFlow(connectivityObserver.isDeviceOnline(application.applicationContext))

    init {
        CoroutineScope(Dispatchers.IO).launch {
            connectivityObserver.observe().collect {
                connectionStatusIsAvailable.value = it == ConnectivityObserver.Status.Available
            }
        }
    }

    override fun getLoadingProgressStateFlow(): StateFlow<Boolean> {
        return loadingProgressStateFlow.asStateFlow()
    }

    override suspend fun invalidateCharactersData() {
        localRepository.deleteAllCharactersData()
    }

    private fun setLoading(isLoading: Boolean) {
        loadingProgressStateFlow.value = isLoading
    }

    override fun getStateFlowIsConnected(): StateFlow<Boolean> {
        return connectionStatusIsAvailable.asStateFlow()
    }

    override fun getFlowAllCharacters(
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): Flow<PagingData<CharacterModel>> {
        val arrayFilters =
            arrayOf(nameFilter, statusFilter, speciesFilter, typeFilter, genderFilter)
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
                        arrayFilters
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
                EpisodesPagingSource { pageIndex ->
                    loadEpisodes(pageIndex, nameFilter, episodeFilter)
                }
            }
        ).flow
    }

    private suspend fun loadEpisodes(
        pageIndex: Int,
        nameFilter: String?,
        episodeFilter: String?
    ): AllEpisodesResponse {
        setLoading(true)
        val localResponse = localRepository.getAllEpisodes(pageIndex, nameFilter, episodeFilter)
        val resultResponse = if (localResponse.listEpisodeInfo == null) {
            remoteRepository.getAllEpisodes(pageIndex, nameFilter, episodeFilter).also {
                localRepository.writeResponseEpisodes(it)
            }
        } else {
            localResponse
        }
        setLoading(false)
        return resultResponse ?: throw RuntimeException("loadEpisodes")
    }

    private suspend fun loadCharacters(
        pageIndex: Int,
        arrayFilters: Array<String?>
    ): AllCharactersResponse {
        setLoading(true)
        val localResponse = localRepository.getAllCharacters(pageIndex, arrayFilters)
        if (pageIndex == 1) {
            checkInvalidData(arrayFilters, localResponse)
        }
        val resultResponse = if (invalidData) {
            remoteRepository.getAllCharacters(
                pageIndex,
                arrayFilters
            ).also {
                localRepository.writeResponseCharacters(
                    it ?: throw RuntimeException("loadCharacters")
                )
            }
        } else {
            localResponse
        }
        setLoading(false)
        return resultResponse ?: throw RuntimeException("loadCharacters")
    }

    private suspend fun checkInvalidData(
        arrayFilters: Array<String?>,
        localResponse: AllCharactersResponse
    ) {
        val firstPage = remoteRepository.getAllCharacters(
            1,
            arrayFilters
        )
        val remoteItems = firstPage?.pageInfo?.countOfElements ?: -1
        val localItems = localResponse.pageInfo?.countOfElements ?: -1
        invalidData = localItems < remoteItems
    }

    private suspend fun loadLocations(
        pageIndex: Int,
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?
    ): AllLocationsResponse? {
        setLoading(true)
        val localResponse =
            localRepository.getAllLocations(pageIndex, nameFilter, typeFilter, dimensionFilter)
        val resultResponse = if (localResponse.listLocationsInfo == null) {
            remoteRepository.getAllLocations(pageIndex, nameFilter, typeFilter, dimensionFilter)
                .also {
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
                localRepository.writeSingleCharacterInfo(remoteResult ?: return null)
                return Mapper.transformCharacterInfoRemoteIntoCharacterDetailsModel(remoteResult)
            }
            return localResult
        } catch (e: java.lang.Exception) {
            null
        }
    }

    override fun getSingleLocationData(id: Int): Single<LocationDetailsModelWithId> {
        return localRepository.getSingleLocationInfoRx(id).flatMap { data ->
            if (data.locationId == null) {
                return@flatMap apiCall.getSingleLocationData(id).map {
                    Mapper.transformLocationInfoRemoteInfoLocationDetailsModelWithIds(it)
                }
            } else {
                Single.create {
                    it.onSuccess(Mapper.transformLocationDtoIntoLocationDetailsWithIds(data))
                }
            }
        }
    }

    override suspend fun getSingleEpisodeData(id: Int): EpisodeDetailsModel? {
        return try {
            val localResult = localRepository.getSingleEpisodeInfo(id)
            var remote: EpisodeInfoRemote? = null
            if (localResult == null) {
                remote = remoteRepository.getSingleEpisodeInfo(id)
                localRepository.writeSingleEpisodeInfo(remote)
            }
            val listId = if (localResult == null) {
                val charactersString = Mapper.transformListStringsToIds(remote?.episodeCharacters)
                Mapper.transformStringIdIntoListInt(charactersString)
            } else {
                localResult.characters
            }
            val charactersData = arrayListOf<CharacterModel>()
            for (charId in listId) {
                val charData = getSingleCharacterData(charId)
                val mappedData =
                    Mapper.transformCharacterDetailsModelIntoCharacterModel(charData) ?: continue
                if (charData != null) charactersData.add(mappedData)
            }
            return EpisodeDetailsModel(
                id = localResult?.id ?: remote?.episodeId ?: return null,
                name = localResult?.name ?: remote?.episodeName ?: return null,
                airDate = localResult?.airDate ?: remote?.episodeAirDate ?: return null,
                episodeNumber = localResult?.episodeNumber ?: remote?.episodeNumber ?: return null,
                characters = charactersData
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
            list?.map { Mapper.transformEpisodeInfoRemoteIntoEpisodeModel(it ?: return null) }
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