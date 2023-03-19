package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote

interface CharactersLocalRepository {

    suspend fun getAllCharacters(
        pageIndex: Int,
        filters: Array<String?>
    ): AllCharactersResponse?
    suspend fun addCharacter(data: CharacterInfoRemote?)
}