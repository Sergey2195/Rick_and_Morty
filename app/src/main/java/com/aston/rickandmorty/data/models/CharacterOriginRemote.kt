package com.aston.rickandmorty.data.models

import com.google.gson.annotations.SerializedName

data class CharacterOriginRemote(
    @SerializedName("name")
    val characterOriginName: String? = null,

    @SerializedName("url")
    val characterOriginUrl: String? = null
)