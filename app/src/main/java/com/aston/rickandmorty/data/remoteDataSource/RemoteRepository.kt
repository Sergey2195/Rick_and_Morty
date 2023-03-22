package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.models.AllCharactersResponse

interface RemoteRepository {
    suspend fun getAllCharacters(
        pageIndex: Int,
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): AllCharactersResponse
}