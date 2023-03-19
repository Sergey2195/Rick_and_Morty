package com.aston.rickandmorty.data.repositoriesImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aston.rickandmorty.data.localDataSource.LocationsLocalRepository
import com.aston.rickandmorty.data.models.AllLocationsResponse
import com.aston.rickandmorty.data.pagingSources.LocationsPagingSource
import com.aston.rickandmorty.data.remoteDataSource.LocationRemoteRepository
import com.aston.rickandmorty.domain.entity.LocationDetailsModelWithId
import com.aston.rickandmorty.domain.entity.LocationModel
import com.aston.rickandmorty.domain.repository.LocationsRepository
import com.aston.rickandmorty.domain.repository.Repository
import com.aston.rickandmorty.mappers.Mapper
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationsRepositoryImpl @Inject constructor(
    private val sharedRepository: Repository,
    private val mapper: Mapper,
    private val applicationScope: CoroutineScope,
    private val pagingConfig: PagingConfig,
    private val localRepository: LocationsLocalRepository,
    private val remoteRepository: LocationRemoteRepository
) : LocationsRepository {

    private var isNotFullData = false

    override fun getFlowAllLocations(
        filters: Array<String?>,
        forceUpdate: Boolean
    ): Flow<PagingData<LocationModel>> {
        return getLocationsPager(filters, forceUpdate).flow
    }

    private fun getLocationsPager(
        filters: Array<String?>,
        forceUpdate: Boolean
    ): Pager<Int, LocationModel> {
        return Pager(pagingConfig) {
            LocationsPagingSource(mapper) { pageIndex ->
                loadLocations(pageIndex, filters, forceUpdate)
            }
        }
    }

    private suspend fun downloadAndUpdateLocationsData(
        pageIndex: Int,
        filters: Array<String?>
    ): AllLocationsResponse? = withContext(Dispatchers.IO) {
        val networkResponse = remoteRepository.getAllLocations(pageIndex, filters)
        if (networkResponse?.listLocationsInfo == null) {
            return@withContext null
        }
        for (location in networkResponse.listLocationsInfo) {
            applicationScope.launch {
                localRepository.addLocation(location)
            }
        }
        return@withContext networkResponse
    }

    private suspend fun loadLocations(
        pageIndex: Int,
        filters: Array<String?>,
        forceUpdate: Boolean
    ): AllLocationsResponse? = withContext(Dispatchers.IO) {
        setLoading(true)
        if (forceUpdate) return@withContext downloadAndUpdateLocationsData(pageIndex, filters)
        val localItems = localRepository.getAllLocations(pageIndex, filters)
        if (pageIndex == 1) {
            val remoteItems = remoteRepository.getAllLocations(1, filters)
            checkIsNotFullData(
                localItems?.pageInfo?.countOfElements,
                remoteItems?.pageInfo?.countOfElements
            )
        }
        return@withContext if (isNotFullData) {
            downloadAndUpdateLocationsData(pageIndex, filters)
        } else {
            localItems
        }
    }.also { setLoading(false) }

    override fun getSingleLocationData(
        id: Int,
        forceUpdate: Boolean
    ): Single<LocationDetailsModelWithId> {
        if (forceUpdate) return getSingleLocationDataWithForceUpdate(id)
        return localRepository.getSingleLocationInfoRx(id).flatMap { data ->
            if (data.locationId == null) {
                return@flatMap remoteRepository.getSingleLocationData(id).map {
                    mapper.transformLocationInfoRemoteInfoLocationDetailsModelWithIds(it)
                }
            } else {
                Single.create {
                    it.onSuccess(mapper.transformLocationDtoIntoLocationDetailsWithIds(data))
                }
            }
        }
    }

    private fun getSingleLocationDataWithForceUpdate(id: Int): Single<LocationDetailsModelWithId> {
        return remoteRepository.getSingleLocationData(id)
            .map { mapper.transformLocationInfoRemoteInfoLocationDetailsModelWithIds(it) }
    }


    override suspend fun getLocationModel(id: Int, forceUpdate: Boolean): LocationModel? =
        withContext(Dispatchers.IO) {
            if (forceUpdate) return@withContext downloadAndUpdateLocationData(id)
            val localData = localRepository.getLocationInfo(id)
                ?: return@withContext downloadAndUpdateLocationData(id)
            mapper.transformLocationInfoRemoteIntoLocationModel(localData)
        }

    private suspend fun downloadAndUpdateLocationData(id: Int): LocationModel? =
        withContext(Dispatchers.IO) {
            val remoteData = remoteRepository.getLocationData(id) ?: return@withContext null
            localRepository.addLocation(remoteData)
            mapper.transformLocationInfoRemoteIntoLocationModel(remoteData)
        }

    override fun getCountOfLocations(filters: Array<String?>): Single<Int> {
        TODO("Not yet implemented")
    }

    private fun setLoading(isLoading: Boolean) {
        sharedRepository.setLoadingProgressStateFlow(isLoading)
    }

    private fun checkIsNotFullData(localItems: Int?, remoteItems: Int?) {
        isNotFullData = (localItems ?: -1) < (remoteItems ?: -1)
    }

}