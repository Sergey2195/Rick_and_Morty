package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.apiCalls.CharactersApiCall
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import io.reactivex.Single
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
        } catch (e: Exception) {
            return null
        }
    }

    override suspend fun getSingleCharacterInfo(id: Int): CharacterInfoRemote? {
        return try {
            apiCall.getSingleCharacterData(id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMultiIdCharacters(request: String): List<CharacterInfoRemote>? {
        return try {
            apiCall.getMultiCharactersData(request)
        } catch (e: Exception) {
            null
        }
    }

    override fun getCountOfCharacters(filters: Array<String?>): Single<Int> {
        return apiCall.getCountOfCharacters(
            filters[0],
            filters[1],
            filters[2],
            filters[3],
            filters[4]
        ).map { it.pageInfo?.countOfElements }
    }
}