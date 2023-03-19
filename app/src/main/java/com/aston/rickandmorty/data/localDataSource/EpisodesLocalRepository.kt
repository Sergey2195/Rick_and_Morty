package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.models.AllEpisodesResponse
import com.aston.rickandmorty.data.models.EpisodeInfoRemote

interface EpisodesLocalRepository {
    suspend fun getAllEpisodes(
        pageIndex: Int,
        filters: Array<String?>
    ): AllEpisodesResponse?

    suspend fun addEpisode(data: EpisodeInfoRemote?)
    suspend fun getEpisodeData(id: Int): EpisodeInfoRemote?
}