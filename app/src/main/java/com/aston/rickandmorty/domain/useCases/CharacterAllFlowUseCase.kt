package com.aston.rickandmorty.domain.useCases

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.repository.Repository
import kotlinx.coroutines.flow.Flow

class CharacterAllFlowUseCase(private val repository: Repository) {
    operator fun invoke(
        nameFilter: String? = null,
        statusFilter: String? = null,
        speciesFilter: String? = null,
        typeFilter: String? = null,
        genderFilter: String? = null
    ): Flow<PagingData<CharacterModel>>{
        return repository.getFlowAllCharacters(nameFilter, statusFilter, speciesFilter, typeFilter, genderFilter)
    }
}