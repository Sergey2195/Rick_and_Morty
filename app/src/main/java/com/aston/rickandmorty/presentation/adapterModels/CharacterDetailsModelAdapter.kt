package com.aston.rickandmorty.presentation.adapterModels

import com.aston.rickandmorty.R
import com.aston.rickandmorty.domain.entity.EpisodeModel
import com.aston.rickandmorty.domain.entity.LocationModel

sealed class CharacterDetailsModelAdapter{
    abstract val viewType: Int
}


data class CharacterDetailsImageModelAdapter(
    override val viewType: Int = R.layout.character_details_image,
    val imageUrl: String?
): CharacterDetailsModelAdapter()

data class CharacterDetailsTitleValueModelAdapter(
    val title: String,
    val value: String,
    override val viewType: Int = R.layout.details_title_value
): CharacterDetailsModelAdapter()

data class CharacterDetailsLocationModelAdapter(
    val locationModel: LocationModel,
    override val viewType: Int = R.layout.location_item
):CharacterDetailsModelAdapter()

data class CharacterDetailsEpisodesModelAdapter(
    val episodesModel: EpisodeModel,
    override val viewType: Int = R.layout.episode_item
):CharacterDetailsModelAdapter()