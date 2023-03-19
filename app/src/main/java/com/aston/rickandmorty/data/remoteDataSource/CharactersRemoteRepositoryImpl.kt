package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.apiCalls.CharactersApiCall
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import javax.inject.Inject

class CharactersRemoteRepositoryImpl @Inject constructor(
    private val apiCall: CharactersApiCall
) : CharactersRemoteRepository {
    override suspend fun getAllCharacters(
        pageIndex: Int,
        arrayFilter: Array<String?>
    ): AllCharactersResponse? {
        return try {
            apiCall.getAllCharacterData(
                pageIndex,
                arrayFilter[0],
                arrayFilter[1],
                arrayFilter[2],
                arrayFilter[3],
                arrayFilter[4]
            )
        }catch (e: Exception){
            return null
        }
    }

    override suspend fun getSingleCharacterInfo(id: Int): CharacterInfoRemote? {
        return try {
            apiCall.getSingleCharacterData(id)
        }catch (e: Exception){
            null
        }
    }
}