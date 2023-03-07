package com.aston.rickandmorty.data.apiCalls

import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.AllLocationsResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.LocationInfoRemote
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiCall {
    @GET("character")
    suspend fun getAllCharacterData(
        @Query("page") page: Int
    ): AllCharactersResponse

    @GET("character/{id}")
    suspend fun getSingleCharacterData(
        @Path("id") id: Int
    ): CharacterInfoRemote

    @GET("location")
    suspend fun getAllLocations(
        @Query("page") page: Int
    ): AllLocationsResponse

    @GET("location/{id}")
    fun getSingleLocationData(
        @Path("id") id: Int
    ): Single<LocationInfoRemote>
}