package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.repository.LocationsRepository
import io.reactivex.Single
import javax.inject.Inject

class CountOfLocationUseCase @Inject constructor(private val repository: LocationsRepository) {
    operator fun invoke(
        nameFilter: String? = null,
        typeFilter: String? = null,
        dimensionFilter: String? = null
    ): Single<Int> =
        repository.getCountOfLocations(arrayOf(nameFilter, typeFilter, dimensionFilter))
}