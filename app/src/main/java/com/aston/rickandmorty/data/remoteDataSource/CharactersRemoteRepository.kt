package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel

interface CharactersRemoteRepository {
    suspend fun getAllCharacters(
        pageIndex: Int,
        arrayFilter: Array<String?>
    ): AllCharactersResponse?

    suspend fun getSingleCharacterInfo(id: Int): CharacterInfoRemote?
}