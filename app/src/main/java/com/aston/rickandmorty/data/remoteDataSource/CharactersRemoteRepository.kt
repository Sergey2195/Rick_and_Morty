package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import io.reactivex.Single

interface CharactersRemoteRepository {
    suspend fun getAllCharacters(
        pageIndex: Int,
        arrayFilter: Array<String?>
    ): AllCharactersResponse?
    suspend fun getSingleCharacterInfo(id: Int): CharacterInfoRemote?
    suspend fun getMultiIdCharacters(request: String): List<CharacterInfoRemote>?
    fun getCountOfCharacters(filters: Array<String?>): Single<Int>
}