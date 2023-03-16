package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.localDataSource.models.LocationInfoDto
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.AllLocationsResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import io.reactivex.Single

interface LocalRepository {
    suspend fun getAllCharacters(
        pageIndex: Int,
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): AllCharactersResponse
    suspend fun deleteAllCharactersData()
    suspend fun getSingleCharacterInfo(id: Int):CharacterDetailsModel?
    suspend fun getSingleLocationInfo(id: Int): LocationInfoDto?
    fun getSingleLocationInfoRx(id: Int): Single<LocationInfoDto?>
    suspend fun getAllLocations(
        pageIndex: Int,
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?
    ): AllLocationsResponse
    suspend fun writeResponse(response: AllCharactersResponse)
    suspend fun writeResponseLocation(response: AllLocationsResponse)
    suspend fun writeSingleCharacterInfo(data: CharacterInfoRemote)
}