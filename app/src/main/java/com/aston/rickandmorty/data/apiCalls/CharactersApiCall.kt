package com.aston.rickandmorty.data.apiCalls

import com.aston.rickandmorty.data.remoteDataSource.models.AllCharactersResponse
import com.aston.rickandmorty.data.remoteDataSource.models.CharacterInfoRemote
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CharactersApiCall {
    @GET("character/")
    suspend fun getAllCharacterData(
        @Query("page") page: Int,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,
        @Query("gender") gender: String? = null
    ): AllCharactersResponse?

    @GET("character/")
    fun getCountOfCharacters(
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,
        @Query("gender") gender: String? = null
    ): Single<AllCharactersResponse>

    @GET("character/{id}")
    suspend fun getSingleCharacterData(
        @Path("id") id: Int
    ): CharacterInfoRemote?

    @GET("character/{multiId}")
    suspend fun getMultiCharactersData(
        @Path("multiId") multiId: String
    ): List<CharacterInfoRemote>
}