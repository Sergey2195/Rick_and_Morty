package com.aston.rickandmorty.data.localDataSource.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_table")
data class CharacterInfoDto (
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
    val characterEpisodes: List<String>? = null,
    val characterUrl: String? = null,
    val characterCreated: String? = null
)