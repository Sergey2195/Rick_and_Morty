package com.aston.rickandmorty.presentation.adapterModels

import com.aston.rickandmorty.R

data class CharacterDetailsModelAdapter(
    val title: String?,
    val value: String?,
    val isClickable: Boolean = false,
    val viewType: Int = R.layout.character_details_item,
    val url: String? = null
)