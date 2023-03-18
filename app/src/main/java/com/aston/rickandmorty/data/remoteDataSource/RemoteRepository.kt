package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.models.*
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.LocationDetailsModel
import io.reactivex.Single

interface RemoteRepository {
    suspend fun getAllCharacters(
        pageIndex: Int,
        arrayFilter: Array<String?>
    ): AllCharactersResponse?

    suspend fun getAllLocations(
        pageIndex: Int,
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?
    ): AllLocationsResponse?

    suspend fun getAllEpisodes(
        pageIndex: Int,
        nameFilter: String?,
        episodeFilter: String?
    ): AllEpisodesResponse?

    suspend fun getSingleCharacterInfo(id: Int): CharacterInfoRemote?
    fun getSingleLocationData(id: Int): Single<LocationInfoRemote>
    suspend fun getSingleEpisodeInfo(id: Int): EpisodeInfoRemote?
    suspend fun getMultiIdCharacters(request: String): List<CharacterInfoRemote>?
}