package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.apiCalls.LocationsApiCall
import com.aston.rickandmorty.data.models.AllLocationsResponse
import com.aston.rickandmorty.data.models.LocationInfoRemote
import io.reactivex.Single
import javax.inject.Inject

class LocationRemoteRepositoryImpl @Inject constructor(
    private val apiCall: LocationsApiCall
): LocationRemoteRepository {
    override suspend fun getAllLocations(
        pageIndex: Int,
        filter: Array<String?>
    ): AllLocationsResponse? {
        return try {
            apiCall.getAllLocations(pageIndex, filter[0], filter[1], filter[2])
        }catch (e: Exception){
            null
        }
    }

    override fun getSingleLocationData(id: Int): Single<LocationInfoRemote> {
        return apiCall.getSingleLocationData(id)
    }

    override suspend fun getLocationData(id: Int): LocationInfoRemote? {
        return try {
            apiCall.getSingleLocationDataCoroutine(id)
        }catch (e: Exception){
            null
        }
    }

    override fun getCountOfLocations(filters: Array<String?>): Single<Int> {
        return apiCall.getCountOfLocations(filters[0], filters[1], filters[2]).map { it.pageInfo?.countOfElements }
    }
}