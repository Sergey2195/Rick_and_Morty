package com.aston.rickandmorty.data.remoteDataSource.models

import com.google.gson.annotations.SerializedName

data class AllCharactersResponse (
    @SerializedName("info")
    val pageInfo: PageInfoResponse? = null,

    @SerializedName("results")
    val listCharactersInfo: List<CharacterInfoRemote>? = null
)