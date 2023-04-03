package com.aston.rickandmorty.domain.entity

data class CharacterDetailsModel(
    val characterId: Int,
    val characterName: String,
    val characterStatus: String,
    val characterSpecies: String,
    val characterType: String,
    val characterGender: String,
    val characterOrigin: CharacterOrigin,
    val characterLocation: CharacterLocation,
    val characterImage: String?,
    val characterEpisodes: List<String>,
    val characterUrl: String?,
    val characterCreated: String
)