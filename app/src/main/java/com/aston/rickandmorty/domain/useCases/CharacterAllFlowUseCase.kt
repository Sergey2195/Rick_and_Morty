package com.aston.rickandmorty.domain.useCases

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CharacterAllFlowUseCase @Inject constructor(private val repository: CharactersRepository) {
    operator fun invoke(
        nameFilter: String? = null,
        statusFilter: String? = null,
        speciesFilter: String? = null,
        typeFilter: String? = null,
        genderFilter: String? = null,
        forceUpdate: Boolean
    ): Flow<PagingData<CharacterModel>> {
        return repository.getFlowAllCharacters(
            arrayOf(
                nameFilter, statusFilter, speciesFilter, typeFilter, genderFilter
            ), forceUpdate
        )
    }
}