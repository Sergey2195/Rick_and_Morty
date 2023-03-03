package com.aston.rickandmorty.mappers

import com.aston.rickandmorty.data.models.CharacterInfo
import com.aston.rickandmorty.domain.entity.CharacterModel

object Mapper {
    fun transformCharacterInfoIntoCharacterModel(src: CharacterInfo): CharacterModel{
        return CharacterModel(
            id = src.characterId ?: 0,
            name = src.characterName ?: EMPTY,
            species = src.characterSpecies ?: EMPTY,
            status = src.characterStatus ?: EMPTY,
            gender = src.characterGender ?: EMPTY,
            image = src.characterImage ?: EMPTY
        )
    }

    fun transformListCharacterInfoIntoListCharacterModel(src: List<CharacterInfo>): List<CharacterModel>{
        return src.map { transformCharacterInfoIntoCharacterModel(it) }
    }

    private const val EMPTY = "placeholder"
}