package com.aston.rickandmorty.domain.useCases

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.EpisodeModel
import com.aston.rickandmorty.domain.repository.Repository
import kotlinx.coroutines.flow.Flow

class EpisodesAllFlowUseCase(private val repository: Repository) {
    operator fun invoke(
        nameFilter: String? = null,
        episodeFilter: String? = null
    ): Flow<PagingData<EpisodeModel>> {
        return repository.getFlowAllEpisodes(nameFilter, episodeFilter)
    }
}