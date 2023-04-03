package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.repository.CharactersRepository
import io.reactivex.Single
import javax.inject.Inject

class CountOfCharactersUseCase @Inject constructor(private val rep: CharactersRepository) {
    operator fun invoke(
        nameFilter: String? = null,
        statusFilter: String? = null,
        speciesFilter: String? = null,
        typeFilter: String? = null,
        genderFilter: String? = null
    ): Single<Int> = rep.getCountOfCharacters(
        arrayOf(
            nameFilter,
            statusFilter,
            speciesFilter,
            typeFilter,
            genderFilter
        )
    )
}