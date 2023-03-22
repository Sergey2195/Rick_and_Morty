package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.remoteDataSource.models.AllEpisodesResponse
import com.aston.rickandmorty.data.remoteDataSource.models.CharacterInfoRemote
import com.aston.rickandmorty.data.remoteDataSource.models.EpisodeInfoRemote
import io.reactivex.Single

interface EpisodesRemoteRepository {
    suspend fun getAllEpisodes(
        pageIndex: Int,
        filters: Array<String?>
    ): AllEpisodesResponse?

    suspend fun getEpisodeInfo(id: Int): EpisodeInfoRemote?
    suspend fun getMultiIdCharacters(request: String): List<CharacterInfoRemote>?
    fun getCountOfEpisodes(filters: Array<String?>): Single<Int>
}