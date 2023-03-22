package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.repository.EpisodesRepository
import javax.inject.Inject

class CountOfEpisodesUseCase @Inject constructor(private val repository: EpisodesRepository) {
    operator fun invoke(nameFilter: String? = null, episodeFilter: String? = null) =
        repository.getCountOfEpisodes(arrayOf(nameFilter, episodeFilter))
}