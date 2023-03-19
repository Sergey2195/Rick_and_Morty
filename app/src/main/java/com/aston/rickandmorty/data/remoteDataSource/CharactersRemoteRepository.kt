package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.models.AllCharactersResponse

interface CharactersRemoteRepository {
    suspend fun getAllCharacters(
        pageIndex: Int,
        arrayFilter: Array<String?>
    ): AllCharactersResponse?
}