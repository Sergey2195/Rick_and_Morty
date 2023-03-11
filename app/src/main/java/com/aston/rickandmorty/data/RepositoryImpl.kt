package com.aston.rickandmorty.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aston.rickandmorty.data.apiCalls.RetrofitApiCall
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.LocationInfoRemote
import com.aston.rickandmorty.data.pagingSources.CharactersPagingSource
import com.aston.rickandmorty.data.pagingSources.EpisodesPagingSource
import com.aston.rickandmorty.data.pagingSources.LocationsPagingSource
import com.aston.rickandmorty.domain.entity.*
import com.aston.rickandmorty.domain.repository.Repository
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.utils.Utils
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

object RepositoryImpl : Repository {

    val apiCall = RetrofitApiCall.getCharacterApiCall()

    override fun getFlowAllCharacters(
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): Flow<PagingData<CharacterModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20),
            pagingSourceFactory = {
                CharactersPagingSource { pageIndex ->
                    apiCall.getAllCharacterData(
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

    override fun getFlowAllLocations(): Flow<PagingData<LocationModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20),
            pagingSourceFactory = { LocationsPagingSource(apiCall) }
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


    override fun getFlowAllEpisodes(): Flow<PagingData<EpisodeModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20),
            pagingSourceFactory = { EpisodesPagingSource(apiCall) }
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
}