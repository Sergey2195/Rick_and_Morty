package com.aston.rickandmorty.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aston.rickandmorty.data.apiCalls.RetrofitApiCall
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.EpisodeInfoRemote
import com.aston.rickandmorty.data.pagingSources.CharactersPagingSource
import com.aston.rickandmorty.data.pagingSources.EpisodesPagingSource
import com.aston.rickandmorty.data.pagingSources.LocationsPagingSource
import com.aston.rickandmorty.domain.entity.*
import com.aston.rickandmorty.domain.repository.Repository
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.utils.Utils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

object RepositoryImpl : Repository {

    val apiCall = RetrofitApiCall.getCharacterApiCall()

    override fun getFlowAllCharacters(): Flow<PagingData<CharacterModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20),
            pagingSourceFactory = { CharactersPagingSource(apiCall) }
        ).flow
    }

    override fun getFlowAllLocations(): Flow<PagingData<LocationModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20),
            pagingSourceFactory = { LocationsPagingSource(apiCall) }
        ).flow
    }

    override suspend fun getSingleCharacterData(id: Int): CharacterDetailsModel? {
        return try {
            val result = apiCall.getSingleCharacterData(id)
            return Mapper.transformCharacterInfoRemoteIntoCharacterDetailsModel(result)
        } catch (e: java.lang.Exception) {
            null
        }
    }

    override fun getSingleLocationData(id: Int): Single<LocationDetailsModel> {
        return apiCall.getSingleLocationData(id).map { data->
            Mapper.transformLocationInfoRemoteIntoLocationDetailsModel(data)
        }
    }

    override fun getFlowAllEpisodes(): Flow<PagingData<EpisodeModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20),
            pagingSourceFactory = { EpisodesPagingSource(apiCall)}
        ).flow
    }

    override suspend fun getSingleEpisodeData(id: Int): EpisodeDetailsModel? {
        Log.d("SSV_TEST", "getSingleEpisodeData")
        return try {
            val result = apiCall.getSingleEpisodeData(id)
            val listId = result.episodeCharacters?.map { Utils.getLastIntAfterSlash(it) } ?: emptyList()
            val requestStr = Utils.getStringForMultiId(listId)
            var listCharactersModel = emptyList<CharacterModel>()
            if (requestStr.isNotBlank()){
                val characters = apiCall.getMultiCharactersData(requestStr)
                listCharactersModel = Mapper.transformListCharacterInfoRemoteIntoCharacterModel(characters)
            }
            return Mapper.transformEpisodeInfoRemoteIntoEpisodeDetailsModel(result, listCharactersModel)
        }catch (e:java.lang.Exception){
            Log.d("SSV_TEST", e.toString())
            null
        }
    }
}