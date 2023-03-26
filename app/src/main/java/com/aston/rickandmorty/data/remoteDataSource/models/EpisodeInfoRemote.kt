package com.aston.rickandmorty.data.remoteDataSource.models

import com.google.gson.annotations.SerializedName

data class EpisodeInfoRemote(
    @SerializedName("id")
    val episodeId: Int? = null,

    @SerializedName("name")
    val episodeName: String? = null,

    @SerializedName("air_date")
    val episodeAirDate: String? = null,

    @SerializedName("episode")
    val episodeNumber: String? = null,

    @SerializedName("characters")
    val episodeCharacters: List<String>? = null,

    @SerializedName("url")
    val episodeUrl: String? = null,

    @SerializedName("created")
    val episodeCreated: String? = null
)