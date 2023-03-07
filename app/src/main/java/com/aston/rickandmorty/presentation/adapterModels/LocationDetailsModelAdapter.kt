package com.aston.rickandmorty.presentation.adapterModels

import com.aston.rickandmorty.R

data class LocationDetailsModelAdapter(
    val title: String?,
    val value: String?,
    val isClickable: Boolean = false,
    val viewType: Int = R.layout.details_title_value,
    val url: String? = null
)