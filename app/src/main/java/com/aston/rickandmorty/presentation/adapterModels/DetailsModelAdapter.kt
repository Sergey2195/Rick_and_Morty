package com.aston.rickandmorty.presentation.adapterModels

import com.aston.rickandmorty.domain.entity.CharacterModel

sealed class DetailsModelAdapter {
    abstract val viewType: Int
}

data class DetailsModelTitleValue(
    val title: String,
    val value: String,
    override val viewType: Int
):DetailsModelAdapter()

data class DetailsModelCharacterList(
    val listCharacters: List<CharacterModel>,
    override val viewType: Int
):DetailsModelAdapter()