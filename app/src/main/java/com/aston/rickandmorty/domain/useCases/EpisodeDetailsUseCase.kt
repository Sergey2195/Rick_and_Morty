package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.entity.EpisodeDetailsModel
import com.aston.rickandmorty.domain.repository.Repository
import javax.inject.Inject

class EpisodeDetailsUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(id: Int, forceUpdate: Boolean): EpisodeDetailsModel?{
        return repository.getSingleEpisodeData(id, forceUpdate)
    }
}