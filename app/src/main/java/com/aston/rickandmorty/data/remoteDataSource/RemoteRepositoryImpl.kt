package com.aston.rickandmorty.data.remoteDataSource

import android.util.Log
import com.aston.rickandmorty.data.apiCalls.ApiCall
import com.aston.rickandmorty.data.models.*
import io.reactivex.Single
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(private val apiCall: ApiCall) : RemoteRepository {
    override suspend fun getAllCharacters(
        pageIndex: Int,
        arrayFilter: Array<String?>
    ): AllCharactersResponse? {
        return try {
            apiCall.getAllCharacterData(
                pageIndex, arrayFilter[0], arrayFilter[1], arrayFilter[2], arrayFilter[3], arrayFilter[4]
            )
        }catch (e: Exception){
            null
        }
    }

    override suspend fun getAllLocations(
        pageIndex: Int,
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?
    ): AllLocationsResponse? {
        return try {
            apiCall.getAllLocations(pageIndex, nameFilter, typeFilter, dimensionFilter)
        }catch (e: Exception){
            null
        }
    }

    override suspend fun getAllEpisodes(
        pageIndex: Int,
        nameFilter: String?,
        episodeFilter: String?
    ): AllEpisodesResponse? {
        return try {
            apiCall.getAllEpisodes(pageIndex, nameFilter, episodeFilter)
        }catch (e: Exception){
            null
        }
    }

    override suspend fun getSingleCharacterInfo(id: Int): CharacterInfoRemote? {
        return apiCall.getSingleCharacterData(id)
    }

    override fun getSingleLocationData(id: Int): Single<LocationInfoRemote> {
        return apiCall.getSingleLocationData(id)
    }

    override suspend fun getSingleEpisodeInfo(id: Int): EpisodeInfoRemote? {
        return apiCall.getSingleEpisodeData(id)
    }

    override suspend fun getMultiIdCharacters(request: String): List<CharacterInfoRemote>?{
        return try {
            apiCall.getMultiCharactersData(request)
        }catch (e: Exception){
            null
        }
    }
}