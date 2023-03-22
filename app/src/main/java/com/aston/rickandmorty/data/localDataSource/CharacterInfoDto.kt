package com.aston.rickandmorty.data.localDataSource

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CharactersTable")
data class CharacterInfoDto(
    @PrimaryKey
    val characterId: Int? = null,
    val characterName: String? = null,
    val characterStatus: String? = null,
    val characterSpecies: String? = null,
    val characterType: String? = null,
    val characterGender: String? = null,
    val characterOriginName: String? = null,
    val characterOriginUrl: String? = null,
    val characterLocationName: String? = null,
    val characterLocationUrl: String? = null,
    val characterImage: String? = null,
    val characterEpisodesIds: String? = null,
    val characterUrl: String? = null,
    val characterCreated: String? = null
)