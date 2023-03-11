package com.aston.rickandmorty.data.apiCalls

import com.aston.rickandmorty.data.models.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiCall {
    @GET("character/")
    suspend fun getAllCharacterData(
        @Query("page") page: Int,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,
        @Query("gender") gender: String? = null
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

    @GET("location/{id}")
    suspend fun getSingleLocationDataCoroutine(
        @Path("id") id: Int
    ): LocationInfoRemote

    @GET("episode")
    suspend fun getAllEpisodes(
        @Query("page") page: Int
    ): AllEpisodesResponse

    @GET("episode/{id}")
    suspend fun getSingleEpisodeData(
        @Path("id") id: Int
    ): EpisodeInfoRemote

    @GET("character/{multiId}")
    suspend fun getMultiCharactersData(
        @Path("multiId") multiId: String
    ): List<CharacterInfoRemote>

    @GET("character/{multiId}")
    fun getMultiCharactersDataRx(
        @Path("multiId") multiId: String
    ): Single<List<CharacterInfoRemote>>

    @GET("episode/{multiId}")
    suspend fun getMultiEpisodesData(
        @Path("multiId") multiId: String
    ): List<EpisodeInfoRemote>?

    @GET("character/{id}")
    fun getCharactersDataRx(
        @Path("id") id: String
    ): Single<CharacterInfoRemote>
}