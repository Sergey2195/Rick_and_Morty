package com.aston.rickandmorty.data.apiCalls

import com.aston.rickandmorty.data.remoteDataSource.models.AllLocationsResponse
import com.aston.rickandmorty.data.remoteDataSource.models.LocationInfoRemote
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LocationsApiCall {
    @GET("location")
    suspend fun getAllLocations(
        @Query("page") page: Int,
        @Query("name") name: String? = null,
        @Query("type") type: String? = null,
        @Query("dimension") dimension: String? = null
    ): AllLocationsResponse?

    @GET("location")
    fun getCountOfLocations(
        @Query("name") name: String? = null,
        @Query("type") type: String? = null,
        @Query("dimension") dimension: String? = null
    ): Single<AllLocationsResponse>

    @GET("location/{id}")
    fun getSingleLocationData(
        @Path("id") id: Int
    ): Single<LocationInfoRemote>

    @GET("location/{id}")
    suspend fun getSingleLocationDataCoroutine(
        @Path("id") id: Int
    ): LocationInfoRemote


}