package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.repository.Repository
import io.reactivex.Single

class CountOfCharactersUseCase(private val repository: Repository) {
    operator fun invoke(
        nameFilter: String? = null,
        statusFilter: String? = null,
        speciesFilter: String? = null,
        typeFilter: String? = null,
        genderFilter: String? = null
    ): Single<Int> = repository.getCountOfCharacters(
        nameFilter,
        statusFilter,
        speciesFilter,
        typeFilter,
        genderFilter
    )
}