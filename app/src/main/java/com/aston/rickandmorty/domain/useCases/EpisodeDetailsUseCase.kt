package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.entity.EpisodeDetailsModel
import com.aston.rickandmorty.domain.repository.Repository

class EpisodeDetailsUseCase(private val repository: Repository) {
    suspend operator fun invoke(id: Int): EpisodeDetailsModel?{
        return repository.getSingleEpisodeData(id)
    }
}