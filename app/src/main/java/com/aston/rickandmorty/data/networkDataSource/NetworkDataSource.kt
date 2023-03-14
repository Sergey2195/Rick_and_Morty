package com.aston.rickandmorty.data.networkDataSource

import com.aston.rickandmorty.data.apiCalls.ApiCall
import com.aston.rickandmorty.data.models.AllCharactersResponse
import javax.inject.Inject

class NetworkDataSource @Inject constructor(private val apiCall: ApiCall) {

    suspend fun getPage(
        pageIndex: Int,
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): AllCharactersResponse {
        return apiCall.getAllCharacterData(
            pageIndex,
            nameFilter,
            statusFilter,
            speciesFilter,
            typeFilter,
            genderFilter
        )
    }
}