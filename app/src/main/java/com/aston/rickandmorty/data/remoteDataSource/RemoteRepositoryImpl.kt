package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.apiCalls.ApiCall
import com.aston.rickandmorty.data.models.AllCharactersResponse
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(private val apiCall: ApiCall) : RemoteRepository {
    override suspend fun getAllCharacters(
        pageIndex: Int,
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): AllCharactersResponse {
        return apiCall.getAllCharacterData(
            pageIndex, nameFilter, statusFilter, speciesFilter, typeFilter, genderFilter
        )
    }
}