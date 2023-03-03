package com.aston.rickandmorty.data.models

import com.google.gson.annotations.SerializedName

data class CharacterInfo(
    @SerializedName("id")
    val characterId: Int? = null,

    @SerializedName("name")
    val characterName: String? = null,

    @SerializedName("status")
    val characterStatus: String? = null,

    @SerializedName("species")
    val characterSpecies: String? = null,

    @SerializedName("type")
    val characterType: String? = null,

    @SerializedName("gender")
    val characterGender: String? = null,

    @SerializedName("origin")
    val characterOrigin: CharacterOrigin? = null,

    @SerializedName("location")
    val characterLocation: CharacterLocation? = null,

    @SerializedName("image")
    val characterImage: String? = null,

    @SerializedName("episode")
    val characterEpisodes: List<String>? = null,

    @SerializedName("url")
    val characterUrl: String? = null,

    @SerializedName("created")
    val characterCreated: String? = null
)