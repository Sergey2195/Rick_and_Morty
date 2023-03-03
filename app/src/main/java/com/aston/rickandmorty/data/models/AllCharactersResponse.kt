package com.aston.rickandmorty.data.models

import com.google.gson.annotations.SerializedName

data class AllCharactersResponse (
    @SerializedName("info")
    val pageInfo: PageInfoResponse? = null,

    @SerializedName("results")
    val listCharactersInfo: List<CharacterInfo>? = null
)