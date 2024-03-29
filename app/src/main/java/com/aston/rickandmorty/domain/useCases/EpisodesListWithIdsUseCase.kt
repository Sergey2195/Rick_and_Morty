package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.entity.EpisodeModel
import com.aston.rickandmorty.domain.repository.EpisodesRepository
import javax.inject.Inject

class EpisodesListWithIdsUseCase @Inject constructor(private val repository: EpisodesRepository) {
    suspend operator fun invoke(multiId: String, forceUpdate: Boolean): List<EpisodeModel>?{
        return repository.getListEpisodeModel(multiId, forceUpdate)
    }
}