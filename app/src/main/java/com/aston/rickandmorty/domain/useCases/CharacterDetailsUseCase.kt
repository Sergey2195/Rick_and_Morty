package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.repository.CharactersRepository
import javax.inject.Inject

class CharacterDetailsUseCase @Inject constructor(private val repository: CharactersRepository) {
    suspend operator fun invoke(id: Int, forceUpdate: Boolean = false): CharacterDetailsModel?{
        return repository.getCharacterData(id, forceUpdate)
    }

    suspend operator fun invoke(listId: List<Int>, forceUpdate: Boolean): List<CharacterModel>{
        return repository.getListCharactersData(listId, forceUpdate)
    }
}