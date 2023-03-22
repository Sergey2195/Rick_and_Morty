package com.aston.rickandmorty.data.remoteDataSource.models

import com.google.gson.annotations.SerializedName

data class AllLocationsResponse(
    @SerializedName("info")
    val pageInfo: PageInfoResponse? = null,

    @SerializedName("results")
    val listLocationsInfo: List<LocationInfoRemote>? = null
)