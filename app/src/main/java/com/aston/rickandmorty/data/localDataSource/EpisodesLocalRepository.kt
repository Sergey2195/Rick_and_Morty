package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.models.AllEpisodesResponse
import com.aston.rickandmorty.data.models.EpisodeInfoRemote
import io.reactivex.Single

interface EpisodesLocalRepository {
    suspend fun getAllEpisodes(
        pageIndex: Int,
        filters: Array<String?>
    ): AllEpisodesResponse?

    suspend fun addEpisode(data: EpisodeInfoRemote?)
    suspend fun getEpisodeData(id: Int): EpisodeInfoRemote?
    fun getCountOfEpisodes(filters: Array<String?>): Single<Int>
}