package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.repository.Repository
import io.reactivex.Single

class CountOfLocationUseCase(private val repository: Repository) {
    operator fun invoke(
        nameFilter: String? = null,
        typeFilter: String? = null,
        dimensionFilter: String? = null
    ): Single<Int> = repository.getCountOfLocations(nameFilter, typeFilter, dimensionFilter)
}