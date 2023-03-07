package com.aston.rickandmorty.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aston.rickandmorty.data.apiCalls.RetrofitApiCall
import com.aston.rickandmorty.data.pagingSources.CharactersPagingSource
import com.aston.rickandmorty.data.pagingSources.LocationsPagingSource
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.entity.LocationDetailsModel
import com.aston.rickandmorty.domain.entity.LocationModel
import com.aston.rickandmorty.domain.repository.Repository
import com.aston.rickandmorty.mappers.Mapper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
}