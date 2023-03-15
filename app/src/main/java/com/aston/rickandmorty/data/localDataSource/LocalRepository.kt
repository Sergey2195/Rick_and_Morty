package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.models.AllCharactersResponse

interface LocalRepository {
    suspend fun getAllCharacters(
        pageIndex: Int,
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): AllCharactersResponse

    suspend fun writeResponse(response: AllCharactersResponse)
}