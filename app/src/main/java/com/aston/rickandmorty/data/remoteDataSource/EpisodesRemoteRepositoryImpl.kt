package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.apiCalls.EpisodesApiCall
import com.aston.rickandmorty.data.remoteDataSource.models.AllEpisodesResponse
import com.aston.rickandmorty.data.remoteDataSource.models.CharacterInfoRemote
import com.aston.rickandmorty.data.remoteDataSource.models.EpisodeInfoRemote
import com.aston.rickandmorty.domain.repository.SharedRepository
import io.reactivex.Single
import javax.inject.Inject

class EpisodesRemoteRepositoryImpl @Inject constructor(
    private val apiCall: EpisodesApiCall,
    private val charactersRemoteRepository: CharactersRemoteRepository,
    private val sharedRepository: SharedRepository
) : EpisodesRemoteRepository {
    override suspend fun getAllEpisodes(
        pageIndex: Int,
        filters: Array<String?>
    ): AllEpisodesResponse? {
        if (!isConnected()) return null
        return try {
            apiCall.getAllEpisodes(pageIndex, filters[0], filters[1])
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getEpisodeInfo(id: Int): EpisodeInfoRemote? {
        if (!isConnected()) return null
        return try {
            apiCall.getSingleEpisodeData(id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMultiIdCharacters(request: String): List<CharacterInfoRemote>? {
        if (!isConnected()) return null
        return charactersRemoteRepository.getMultiIdCharacters(request)
    }

    override fun getCountOfEpisodes(filters: Array<String?>): Single<Int> {
        if (!isConnected()) return Single.error(Exception("no connection"))
        return apiCall.getCountOfEpisodes(filters[0], filters[1]).map { it.pageInfo?.countOfElements}
    }

    private fun isConnected(): Boolean{
        return sharedRepository.getStateFlowIsConnected().value
    }
}