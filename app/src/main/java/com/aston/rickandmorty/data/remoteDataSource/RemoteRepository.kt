package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.LocationInfoRemote
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.LocationDetailsModel
import io.reactivex.Single

interface RemoteRepository {
    suspend fun getAllCharacters(
        pageIndex: Int,
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): AllCharactersResponse

    suspend fun getSingleCharacterInfo(id: Int): CharacterInfoRemote
    fun getSingleLocationData(id: Int): Single<LocationInfoRemote>
}