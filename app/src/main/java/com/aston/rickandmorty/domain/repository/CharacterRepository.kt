package com.aston.rickandmorty.domain.repository

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.CharacterModel
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getFlowAllCharacters(): Flow<PagingData<CharacterModel>>
    suspend fun getSingleCharacterData(id: Int): CharacterDetailsModel?
}