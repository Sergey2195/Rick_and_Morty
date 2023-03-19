package com.aston.rickandmorty.domain.repository

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.*
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Repository {
    fun getStateFlowIsConnected(): StateFlow<Boolean>
    fun getCountOfCharacters(
        nameFilter: String? = null,
        statusFilter: String? = null,
        speciesFilter: String? = null,
        typeFilter: String? = null,
        genderFilter: String? = null
    ): Single<Int>

    fun getCountOfLocations(
        nameFilter: String? = null,
        typeFilter: String? = null,
        dimensionFilter: String? = null
    ): Single<Int>

    fun getCountOfEpisodes(
        nameFilter: String? = null,
        episodeFilter: String? = null
    ): Single<Int>

    fun getLoadingProgressStateFlow(): StateFlow<Boolean>
    fun setLoadingProgressStateFlow(isLoading: Boolean)
}