package com.aston.rickandmorty.domain.repository

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.CharacterModel
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {
    fun getFlowAllCharacters(
        filters: Array<String?>,
        forceUpdate: Boolean
    ): Flow<PagingData<CharacterModel>>

    suspend fun getCharacterData(id: Int, forceUpdate: Boolean): CharacterDetailsModel?
    fun getCountOfCharacters(filters: Array<String?>): Single<Int>
    suspend fun getMultiCharacterModel(multiId: String): List<CharacterModel>
}