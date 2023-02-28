package com.aston.rickandmorty.domain.entity

data class CharacterModel(
    val id: Int,
    val name: String,
    val species: String,
    val status: String,
    val gender: String
)