package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.localDataSource.models.LocationInfoDto
import com.aston.rickandmorty.data.remoteDataSource.models.AllLocationsResponse
import com.aston.rickandmorty.data.remoteDataSource.models.LocationInfoRemote
import io.reactivex.Single

interface LocationsLocalRepository {
    suspend fun addLocation(location: LocationInfoRemote?)
    suspend fun getAllLocations(
        pageIndex: Int,
        filter: Array<String?>
    ): AllLocationsResponse?
    fun getSingleLocationInfoRx(id: Int): Single<LocationInfoDto>
    suspend fun getLocationInfo(id: Int): LocationInfoRemote?
    fun getCountOfLocations(filters: Array<String?>): Single<Int>
}