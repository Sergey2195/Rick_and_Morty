package com.aston.rickandmorty.presentation.adapterModels

import com.aston.rickandmorty.domain.entity.CharacterModel

sealed class EpisodeDetailsModelAdapter {
    abstract val viewType: Int
}

data class EpisodeDetailsModelTitleValue(
    val title: String,
    val value: String,
    override val viewType: Int
):EpisodeDetailsModelAdapter()

data class EpisodeDetailsModelCharacterList(
    val listCharacters: List<CharacterModel>,
    override val viewType: Int
):EpisodeDetailsModelAdapter()