package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.repository.CharactersRepository
import com.aston.rickandmorty.domain.repository.Repository
import javax.inject.Inject

class CharacterDetailsUseCase @Inject constructor(private val repository: CharactersRepository) {
    suspend operator fun invoke(id: Int, forceUpdate: Boolean = false): CharacterDetailsModel?{
        return repository.getCharacterData(id, forceUpdate)
    }
}