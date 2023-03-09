package com.aston.rickandmorty.domain.repository

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.*
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getFlowAllCharacters(): Flow<PagingData<CharacterModel>>
    fun getFlowAllLocations(): Flow<PagingData<LocationModel>>
    suspend fun getSingleCharacterData(id: Int): CharacterDetailsModel?
    fun getSingleLocationData(id: Int): Single<LocationDetailsModel>
    fun getFlowAllEpisodes(): Flow<PagingData<EpisodeModel>>
}