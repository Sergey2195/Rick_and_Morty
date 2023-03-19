package com.aston.rickandmorty.data.remoteDataSource

import com.aston.rickandmorty.data.apiCalls.CharactersApiCall
import com.aston.rickandmorty.data.apiCalls.EpisodesApiCall
import com.aston.rickandmorty.data.models.AllEpisodesResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.EpisodeInfoRemote
import javax.inject.Inject

class EpisodesRemoteRepositoryImpl @Inject constructor(
    private val apiCall: EpisodesApiCall,
    private val charactersApiCall: CharactersApiCall
): EpisodesRemoteRepository {
    override suspend fun getAllEpisodes(
        pageIndex: Int,
        filters: Array<String?>
    ): AllEpisodesResponse? {
        return try {
            apiCall.getAllEpisodes(pageIndex, filters[0], filters[1])
        }catch (e: Exception){
            null
        }
    }

    override suspend fun getSingleEpisodeInfo(id: Int): EpisodeInfoRemote? {
        return try {
            apiCall.getSingleEpisodeData(id)
        }catch (e: Exception){
            null
        }
    }

    override suspend fun getMultiIdCharacters(request: String): List<CharacterInfoRemote>? {
        return try {
            charactersApiCall.getMultiCharactersData(request)
        }catch (e: Exception){
            null
        }
    }
}