package com.aston.rickandmorty.domain.entity

data class LocationDetailsModel(
    val locationId: Int,
    val locationName: String,
    val locationType: String,
    val dimension: String,
    val characters: List<CharacterModel>
)