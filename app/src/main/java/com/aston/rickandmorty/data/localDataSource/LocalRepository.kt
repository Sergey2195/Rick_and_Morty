package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.localDataSource.models.LocationInfoDto
import com.aston.rickandmorty.data.models.*
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.EpisodeDetailsModel
import com.aston.rickandmorty.domain.entity.EpisodeDetailsModelWithId
import io.reactivex.Single

interface LocalRepository {
    suspend fun getAllCharacters(
        pageIndex: Int,
        arrayFilter: Array<String?>
    ): AllCharactersResponse
    suspend fun deleteAllCharactersData()
    suspend fun getSingleCharacterInfo(id: Int):CharacterDetailsModel?
    suspend fun getSingleEpisodeInfo(id: Int): EpisodeDetailsModelWithId?
    suspend fun getSingleLocationInfo(id: Int): LocationInfoDto?
    fun getSingleLocationInfoRx(id: Int): Single<LocationInfoDto>
    suspend fun getAllLocations(
        pageIndex: Int,
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?
    ): AllLocationsResponse
    suspend fun getAllEpisodes(
        pageIndex: Int,
        nameFilter: String?,
        episodeFilter: String?
    ): AllEpisodesResponse
    suspend fun writeResponseCharacters(response: AllCharactersResponse)
    suspend fun writeResponseLocation(response: AllLocationsResponse?)
    suspend fun writeResponseEpisodes(response: AllEpisodesResponse?)
    suspend fun writeSingleCharacterInfo(data: CharacterInfoRemote)
    suspend fun writeSingleEpisodeInfo(data: EpisodeInfoRemote?)
}