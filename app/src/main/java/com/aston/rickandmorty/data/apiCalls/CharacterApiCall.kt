package com.aston.rickandmorty.data.apiCalls

import com.aston.rickandmorty.data.models.AllCharactersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CharacterApiCall {
    @GET("character")
    suspend fun getAllCharacterData(
        @Query("page") page: Int
    ): AllCharactersResponse
}