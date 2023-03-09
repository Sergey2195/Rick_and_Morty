package com.aston.rickandmorty.data.models

import com.google.gson.annotations.SerializedName

data class AllEpisodesResponse(
    @SerializedName("info")
    val pageInfo: PageInfoResponse? = null,

    @SerializedName("results")
    val listEpisodeInfo: List<EpisodeInfoRemote>? = null
)