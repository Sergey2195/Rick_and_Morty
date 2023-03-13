package com.aston.rickandmorty.data.models

import com.google.gson.annotations.SerializedName

data class CharacterLocationRemote(
    @SerializedName("name")
    val characterLocationName: String? = null,

    @SerializedName("url")
    val characterLocationUrl: String? = null
)