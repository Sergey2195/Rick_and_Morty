package com.aston.rickandmorty.data.models

import com.google.gson.annotations.SerializedName

data class PageInfoResponse(
    @SerializedName("count")
    val countOfElements: Int? = null,

    @SerializedName("pages")
    val countPages: Int? = null,

    @SerializedName("next")
    val nextPageUrl: String? = null,

    @SerializedName("prev")
    val prevPageUrl: String? = null
)