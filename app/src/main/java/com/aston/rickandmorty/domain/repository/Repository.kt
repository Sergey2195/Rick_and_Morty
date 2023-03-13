package com.aston.rickandmorty.domain.repository

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.*
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getFlowAllCharacters(
        nameFilter: String? = null,
        statusFilter: String? = null,
        speciesFilter: String? = null,
        typeFilter: String? = null,
        genderFilter: String? = null
    ): Flow<PagingData<CharacterModel>>

    fun getFlowAllLocations(
        nameFilter: String? = null,
        typeFilter: String? = null,
        dimensionFilter: String? = null
    ): Flow<PagingData<LocationModel>>

    suspend fun getSingleCharacterData(id: Int): CharacterDetailsModel?
    fun getSingleLocationData(id: Int): Single<LocationDetailsModel>
    fun getFlowAllEpisodes(
        nameFilter: String? = null,
        episodeFilter: String? = null
    ): Flow<PagingData<EpisodeModel>>

    suspend fun getSingleEpisodeData(id: Int): EpisodeDetailsModel?
    suspend fun getLocationModel(id: Int): LocationModel?
    suspend fun getListEpisodeModel(multiId: String): List<EpisodeModel>?
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
}