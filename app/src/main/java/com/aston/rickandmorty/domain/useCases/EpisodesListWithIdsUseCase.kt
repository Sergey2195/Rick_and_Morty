package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.entity.EpisodeModel
import com.aston.rickandmorty.domain.repository.Repository
import javax.inject.Inject

class EpisodesListWithIdsUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(multiId: String): List<EpisodeModel>?{
        return repository.getListEpisodeModel(multiId)
    }
}