package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.apiCalls.ApiCall
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.LocationInfoRemote
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.LocationDetailsModel
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.utils.Utils
import io.reactivex.Single
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

    override suspend fun getSingleCharacterInfo(id: Int): CharacterInfoRemote{
        return apiCall.getSingleCharacterData(id)
    }

    override fun getSingleLocationData(id: Int): Single<LocationInfoRemote> {
        var locationInfoRemote: LocationInfoRemote? = null
        return apiCall.getSingleLocationData(id)
    }
}