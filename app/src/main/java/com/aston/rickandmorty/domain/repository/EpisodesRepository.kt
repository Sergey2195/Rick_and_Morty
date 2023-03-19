package com.aston.rickandmorty.domain.repository

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.EpisodeDetailsModel
import com.aston.rickandmorty.domain.entity.EpisodeModel
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface EpisodesRepository {
    fun getFlowAllEpisodes(
        filters: Array<String?>,
        forceUpdate: Boolean
    ): Flow<PagingData<EpisodeModel>>

    suspend fun getEpisodeData(id: Int, forceUpdate: Boolean): EpisodeDetailsModel?
    suspend fun getListEpisodeModel(multiId: String): List<EpisodeModel>?
    fun getCountOfEpisodes(filters: Array<String?>): Single<Int>
}