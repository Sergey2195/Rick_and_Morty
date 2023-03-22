package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.apiCalls.EpisodesApiCall
import com.aston.rickandmorty.data.models.AllEpisodesResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.EpisodeInfoRemote
import io.reactivex.Single
import javax.inject.Inject

class EpisodesRemoteRepositoryImpl @Inject constructor(
    private val apiCall: EpisodesApiCall,
    private val charactersRemoteRepository: CharactersRemoteRepository
) : EpisodesRemoteRepository {
    override suspend fun getAllEpisodes(
        pageIndex: Int,
        filters: Array<String?>
    ): AllEpisodesResponse? {
        return try {
            apiCall.getAllEpisodes(pageIndex, filters[0], filters[1])
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getEpisodeInfo(id: Int): EpisodeInfoRemote? {
        return try {
            apiCall.getSingleEpisodeData(id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMultiIdCharacters(request: String): List<CharacterInfoRemote>? {
        return charactersRemoteRepository.getMultiIdCharacters(request)
    }

    override fun getCountOfEpisodes(filters: Array<String?>): Single<Int> {
        return apiCall.getCountOfEpisodes(filters[0], filters[1]).map { it.pageInfo?.countOfElements}
    }
}