package com.aston.rickandmorty.domain.entity

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getFlowAllCharacters(): Flow<PagingData<CharacterModel>>
}