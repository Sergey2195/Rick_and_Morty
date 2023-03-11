package com.aston.rickandmorty.presentation.adapterModels

import com.aston.rickandmorty.R
import com.aston.rickandmorty.domain.entity.CharacterModel

sealed class DetailsModelAdapter {
    abstract val viewType: Int
}

data class DetailsModelText(
    val text: String,
    override val viewType: Int = R.layout.details_text
) : DetailsModelAdapter()

data class DetailsModelCharacter(
    val characterData: CharacterModel,
    override val viewType: Int = R.layout.character_item
) : DetailsModelAdapter()