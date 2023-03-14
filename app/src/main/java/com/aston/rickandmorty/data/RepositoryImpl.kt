package com.aston.rickandmorty.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aston.rickandmorty.data.apiCalls.ApiCall
import com.aston.rickandmorty.data.apiCalls.RetrofitApiCall
import com.aston.rickandmorty.data.localDataSource.LocalDataSource
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.LocationInfoRemote
import com.aston.rickandmorty.data.networkDataSource.NetworkDataSource
import com.aston.rickandmorty.data.pagingSources.CharactersPagingSource
import com.aston.rickandmorty.data.pagingSources.EpisodesPagingSource
import com.aston.rickandmorty.data.pagingSources.LocationsPagingSource
import com.aston.rickandmorty.domain.entity.*
import com.aston.rickandmorty.domain.repository.Repository
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.utils.Utils
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource,
    private val apiCall: ApiCall
) : Repository {

    override fun getFlowAllCharacters(
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): Flow<PagingData<CharacterModel>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                CharactersPagingSource { pageIndex ->
                    Log.d("SSV_L", "$pageIndex")
                    var resultPage = localDataSource.checkContainsInDb(
                        pageIndex,
                        nameFilter,
                        statusFilter,
                        speciesFilter,
                        typeFilter,
                        genderFilter
                    )
                    if (resultPage?.pageInfo?.nextPageUrl == null) {
                        resultPage?.pageInfo?.nextPageUrl = "${pageIndex + 1}"
                    }
//                    if (resultPage == null){
//                        return@CharactersPagingSource networkDataSource.getPage(pageIndex, nameFilter, statusFilter, speciesFilter, typeFilter, genderFilter)
//                    }
//                    return@CharactersPagingSource resultPage
                    val networkPage = networkDataSource.getPage(
                        pageIndex,
                        nameFilter,
                        statusFilter,
                        speciesFilter,
                        typeFilter,
                        genderFilter
                    )
                    writeToDataBase(networkPage)
                    return@CharactersPagingSource networkPage
                }
            }
        ).flow
    }

    private fun writeToDataBase(allCharactersResponse: AllCharactersResponse) {
        localDataSource.writeToDataBase(allCharactersResponse)
    }

    override fun getFlowAllLocations(
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?
    ): Flow<PagingData<LocationModel>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                LocationsPagingSource { pageIndex ->
                    apiCall.getAllLocations(
                        pageIndex,
                        nameFilter,
                        typeFilter,
                        dimensionFilter
                    )
                }
            }
        ).flow
    }

    override suspend fun getSingleCharacterData(id: Int): CharacterDetailsModel? {
        return try {
            val result = apiCall.getSingleCharacterData(id)
            return Mapper.transformCharacterInfoRemoteIntoCharacterDetailsModel(result)
        } catch (e: java.lang.Exception) {
            null
        }
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

    override fun getSingleLocationData(id: Int): Single<LocationDetailsModel> {
        var locationInfoRemote: LocationInfoRemote? = null
        return apiCall.getSingleLocationData(id)
            .flatMap { data ->
                locationInfoRemote = data
                val listId =
                    data.locationResidents?.map { Utils.getLastIntAfterSlash(it) } ?: emptyList()
                val requestStr = Utils.getStringForMultiId(listId)
                if (requestStr.contains(',')) {
                    apiCall.getMultiCharactersDataRx(requestStr)
                } else {
                    apiCall.getCharactersDataRx(requestStr)
                }
            }.map { data ->
                val inputList: List<CharacterInfoRemote> = when (data) {
                    is List<*> -> data as List<CharacterInfoRemote>
                    else -> listOf(data) as List<CharacterInfoRemote>
                }
                val list = Mapper.transformListCharacterInfoRemoteIntoCharacterModel(inputList)
                Mapper.transformLocationInfoRemoteIntoLocationDetailsModel(
                    locationInfoRemote!!,
                    list
                )
            }
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

    companion object{
        private const val PAGE_SIZE = 1
    }
}