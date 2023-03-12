package com.aston.rickandmorty.domain.entity

data class CharacterFilterModel(
    var nameFilter: String? = null,
    var statusFilter: String? = null,
    var speciesFilter: String? = null,
    var typeFilter: String? = null,
    var genderFilter: String? = null
)