package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.repository.CharacterRepository

class CharacterDetailsUseCase(private val repository: CharacterRepository) {
    suspend operator fun invoke(id: Int): CharacterDetailsModel?{
        return repository.getSingleCharacterData(id)
    }
}