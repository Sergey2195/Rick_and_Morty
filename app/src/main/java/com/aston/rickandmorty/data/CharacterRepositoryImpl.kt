package com.aston.rickandmorty.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aston.rickandmorty.data.apiCalls.RetrofitApiCall
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.repository.CharacterRepository
import com.aston.rickandmorty.mappers.Mapper
import kotlinx.coroutines.flow.Flow

object CharacterRepositoryImpl: CharacterRepository {

    val characterApiCall = RetrofitApiCall.getCharacterApiCall()

    override fun getFlowAllCharacters(): Flow<PagingData<CharacterModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20),
            pagingSourceFactory = { CharactersPagingSource(characterApiCall) }
        ).flow
    }

    override suspend fun getSingleCharacterData(id: Int): CharacterDetailsModel? {
        return try {
            val result = characterApiCall.getSingleCharacterData(id)
            return Mapper.transformCharacterInfoRemoteIntoCharacterDetailsModel(result)
        }catch (e: java.lang.Exception){
            null
        }
    }
}