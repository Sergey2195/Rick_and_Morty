package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.apiCalls.CharactersApiCall
import com.aston.rickandmorty.data.remoteDataSource.models.AllCharactersResponse
import com.aston.rickandmorty.data.remoteDataSource.models.CharacterInfoRemote
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.repository.SharedRepository
import io.reactivex.Single
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

@ApplicationScope
class CharactersRemoteRepositoryImpl @Inject constructor(
    private val apiCall: CharactersApiCall,
    private val sharedRepository: SharedRepository
) : CharactersRemoteRepository {

    override suspend fun getAllCharacters(
        pageIndex: Int,
        arrayFilter: Array<String?>
    ): AllCharactersResponse? {
        if (!isConnected()) return null
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
            connectionError(e)
            return null
        }
    }

    override suspend fun getSingleCharacterInfo(id: Int): CharacterInfoRemote? {
        if (!isConnected()) return null
        return try {
            apiCall.getSingleCharacterData(id)
        } catch (e: Exception) {
            connectionError(e)
            null
        }
    }

    override suspend fun getMultiIdCharacters(request: String): List<CharacterInfoRemote>? {
        if (!isConnected()) return null
        return try {
            apiCall.getMultiCharactersData(request)
        } catch (e: Exception) {
            connectionError(e)
            null
        }
    }

    override fun getCountOfCharacters(filters: Array<String?>): Single<Int> {
        if (!isConnected()) return Single.error(Exception("no connection"))
        return apiCall.getCountOfCharacters(
            filters[0],
            filters[1],
            filters[2],
            filters[3],
            filters[4]
        ).map { it.pageInfo?.countOfElements ?: -1 }
            .doOnError { connectionError(Exception(it)) }
    }

    private fun isConnected(): Boolean {
        return sharedRepository.getStateFlowIsConnected().value
    }

    private fun connectionError(e: Exception) {
        sharedRepository.errorConnection(e)
    }
}