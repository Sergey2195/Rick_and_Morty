package com.aston.rickandmorty.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class BottomSheetInputData(
    val prevSearch: String? = null
) : Parcelable