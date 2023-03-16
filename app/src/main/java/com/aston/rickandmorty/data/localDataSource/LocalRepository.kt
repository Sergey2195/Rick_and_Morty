package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel

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
    suspend fun writeResponse(response: AllCharactersResponse)
    suspend fun writeSingleCharacterInfo(data: CharacterInfoRemote)
}