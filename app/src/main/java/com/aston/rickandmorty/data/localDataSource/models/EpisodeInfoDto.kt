package com.aston.rickandmorty.data.localDataSource.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "EpisodesTable")
data class EpisodeInfoDto(
    @PrimaryKey
    val episodeId: Int? = null,
    val episodeName: String? = null,
    val episodeAirDate: String? = null,
    val episodeNumber: String? = null,
    val episodeCharacters: String? = null,
    val episodeUrl: String? = null,
    val episodeCreated: String? = null
)