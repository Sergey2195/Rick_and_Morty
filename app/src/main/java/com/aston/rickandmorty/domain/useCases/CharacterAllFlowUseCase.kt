package com.aston.rickandmorty.domain.useCases

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow

class CharacterAllFlowUseCase(private val repository: CharacterRepository) {
    operator fun invoke(): Flow<PagingData<CharacterModel>>{
        return repository.getFlowAllCharacters()
    }
}