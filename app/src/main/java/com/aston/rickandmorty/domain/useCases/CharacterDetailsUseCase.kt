package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.repository.Repository

class CharacterDetailsUseCase(private val repository: Repository) {
    suspend operator fun invoke(id: Int): CharacterDetailsModel?{
        return repository.getSingleCharacterData(id)
    }
}