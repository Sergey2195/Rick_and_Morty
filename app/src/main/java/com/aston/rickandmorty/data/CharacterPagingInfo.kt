package com.aston.rickandmorty.data

import com.aston.rickandmorty.domain.entity.CharacterModel

data class CharacterPagingInfo(
    val listData: List<CharacterModel>,
    val prevPage: Int? = null,
    val nextPage: Int? = null
)