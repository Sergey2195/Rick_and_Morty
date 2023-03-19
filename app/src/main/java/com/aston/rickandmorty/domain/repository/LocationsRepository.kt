package com.aston.rickandmorty.domain.repository

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.LocationDetailsModelWithId
import com.aston.rickandmorty.domain.entity.LocationModel
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface LocationsRepository {
    fun getFlowAllLocations(
        filters: List<String?>?,
        forceUpdate: Boolean
    ): Flow<PagingData<LocationModel>>

    fun getSingleLocationData(id: Int, forceUpdate: Boolean): Single<LocationDetailsModelWithId>
    suspend fun getLocationModel(id: Int, forceUpdate: Boolean): LocationModel?
    fun getCountOfLocations(
        filters: List<String?>?
    ): Single<Int>
}