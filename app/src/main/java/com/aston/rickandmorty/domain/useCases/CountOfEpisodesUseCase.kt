package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.repository.Repository
import javax.inject.Inject

class CountOfEpisodesUseCase @Inject constructor(private val repository: Repository) {
    operator fun invoke(nameFilter: String? = null, episodeFilter: String? = null) =
        repository.getCountOfEpisodes(nameFilter, episodeFilter)
}