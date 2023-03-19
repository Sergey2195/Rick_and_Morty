package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.models.AllLocationsResponse
import com.aston.rickandmorty.data.models.LocationInfoRemote
import io.reactivex.Single

interface LocationRemoteRepository {
    suspend fun getAllLocations(
        pageIndex: Int,
        filter: Array<String?>
    ): AllLocationsResponse?

    fun getSingleLocationData(id: Int): Single<LocationInfoRemote>
    suspend fun getLocationData(id: Int): LocationInfoRemote?
}