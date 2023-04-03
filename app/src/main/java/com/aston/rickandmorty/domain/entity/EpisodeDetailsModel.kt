package com.aston.rickandmorty.domain.entity

data class EpisodeDetailsModel(
    val id: Int,
    val name: String,
    val airDate: String,
    val episodeNumber: String,
    val characters: List<CharacterModel>
)