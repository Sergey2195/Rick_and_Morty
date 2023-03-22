package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.localDataSource.models.CharacterInfoDto
import com.aston.rickandmorty.data.remoteDataSource.models.AllCharactersResponse
import com.aston.rickandmorty.data.remoteDataSource.models.CharacterInfoRemote
import io.reactivex.Single

interface CharactersLocalRepository {

    suspend fun getAllCharacters(
        pageIndex: Int,
        filters: Array<String?>
    ): AllCharactersResponse?
    suspend fun addCharacter(data: CharacterInfoRemote?)
    suspend fun getCharacterInfo(id: Int): CharacterInfoDto?
    fun getCountOfCharacters(filters: Array<String?>): Single<Int>
}