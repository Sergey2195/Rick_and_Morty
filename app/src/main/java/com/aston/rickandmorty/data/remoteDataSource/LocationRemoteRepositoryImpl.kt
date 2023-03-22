package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.apiCalls.LocationsApiCall
import com.aston.rickandmorty.data.remoteDataSource.models.AllLocationsResponse
import com.aston.rickandmorty.data.remoteDataSource.models.LocationInfoRemote
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.repository.SharedRepository
import io.reactivex.Single
import javax.inject.Inject

@ApplicationScope
class LocationRemoteRepositoryImpl @Inject constructor(
    private val apiCall: LocationsApiCall,
    private val sharedRepository: SharedRepository
): LocationRemoteRepository {

    override suspend fun getAllLocations(
        pageIndex: Int,
        filter: Array<String?>
    ): AllLocationsResponse? {
        if (!isConnected()) return null
        return try {
            apiCall.getAllLocations(pageIndex, filter[0], filter[1], filter[2])
        }catch (e: Exception){
            null
        }
    }

    override fun getSingleLocationData(id: Int): Single<LocationInfoRemote> {
        if (!isConnected()) return Single.error(Exception("no connection"))
        return apiCall.getSingleLocationData(id)
    }

    override suspend fun getLocationData(id: Int): LocationInfoRemote? {
        if (!isConnected()) return null
        return try {
            apiCall.getSingleLocationDataCoroutine(id)
        }catch (e: Exception){
            null
        }
    }

    override fun getCountOfLocations(filters: Array<String?>): Single<Int> {
        if (!isConnected()) return Single.error(Exception("no connection"))
        return apiCall.getCountOfLocations(filters[0], filters[1], filters[2]).map { it.pageInfo?.countOfElements }
    }

    private fun isConnected(): Boolean{
        return sharedRepository.getStateFlowIsConnected().value
    }
}