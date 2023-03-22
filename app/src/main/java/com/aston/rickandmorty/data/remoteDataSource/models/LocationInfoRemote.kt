package com.aston.rickandmorty.data.remoteDataSource.models

import com.google.gson.annotations.SerializedName

data class LocationInfoRemote(
    @SerializedName("id")
    val locationId: Int? = null,

    @SerializedName("name")
    val locationName: String? = null,

    @SerializedName("type")
    val locationType: String? = null,

    @SerializedName("dimension")
    val locationDimension: String? = null,

    @SerializedName("residents")
    val locationResidents: List<String>? = null,

    @SerializedName("url")
    val locationUrl: String? = null,

    @SerializedName("created")
    val locationCreated: String? = null
)