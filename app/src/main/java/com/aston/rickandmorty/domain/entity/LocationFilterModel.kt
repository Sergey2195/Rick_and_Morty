package com.aston.rickandmorty.domain.entity

data class LocationFilterModel(
    var nameFilter: String? = null,
    var typeFilter: String? = null,
    var dimensionFilter: String? = null
)