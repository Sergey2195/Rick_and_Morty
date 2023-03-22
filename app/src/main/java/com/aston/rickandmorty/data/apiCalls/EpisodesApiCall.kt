package com.aston.rickandmorty.data.apiCalls

import com.aston.rickandmorty.data.remoteDataSource.models.AllEpisodesResponse
import com.aston.rickandmorty.data.remoteDataSource.models.EpisodeInfoRemote
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EpisodesApiCall {
    @GET("episode")
    suspend fun getAllEpisodes(
        @Query("page") page: Int,
        @Query("name") name: String? = null,
        @Query("episode") episode: String? = null
    ): AllEpisodesResponse?

    @GET("episode")
    fun getCountOfEpisodes(
        @Query("name") name: String? = null,
        @Query("episode") episode: String? = null
    ): Single<AllEpisodesResponse>

    @GET("episode/{id}")
    suspend fun getSingleEpisodeData(
        @Path("id") id: Int
    ): EpisodeInfoRemote?
}