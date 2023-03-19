package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.models.AllEpisodesResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.EpisodeInfoRemote

interface EpisodesRemoteRepository {
    suspend fun getAllEpisodes(
        pageIndex: Int,
        filters: Array<String?>
    ): AllEpisodesResponse?

    suspend fun getSingleEpisodeInfo(id: Int): EpisodeInfoRemote?
    suspend fun getMultiIdCharacters(request: String): List<CharacterInfoRemote>?
}