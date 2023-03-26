package com.aston.rickandmorty.domain.useCases

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.EpisodeModel
import com.aston.rickandmorty.domain.repository.EpisodesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EpisodesAllFlowUseCase @Inject constructor(private val repository: EpisodesRepository) {
    operator fun invoke(
        nameFilter: String? = null,
        episodeFilter: String? = null,
        forceUpdate: Boolean
    ): Flow<PagingData<EpisodeModel>> {
        return repository.getFlowAllEpisodes(arrayOf(nameFilter, episodeFilter), forceUpdate)
    }
}