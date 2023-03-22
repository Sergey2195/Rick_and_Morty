package com.aston.rickandmorty.data.localDataSource

import android.util.Log
import com.aston.rickandmorty.data.localDataSource.LocalRepositoriesUtils.Companion.PAGE_SIZE
import com.aston.rickandmorty.data.localDataSource.dao.LocationsDao
import com.aston.rickandmorty.data.localDataSource.models.LocationInfoDto
import com.aston.rickandmorty.data.models.AllLocationsResponse
import com.aston.rickandmorty.data.models.LocationInfoRemote
import com.aston.rickandmorty.data.models.PageInfoResponse
import com.aston.rickandmorty.mappers.Mapper
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationLocalRepositoryImpl @Inject constructor(
    private val mapper: Mapper,
    private val locationsDao: LocationsDao,
    private val applicationScope: CoroutineScope,
    private val utils: LocalRepositoriesUtils
) : LocationsLocalRepository {

    private var allLocationData: List<LocationInfoDto> = emptyList()

    init {
        observeAllLocation()
    }

    private fun observeAllLocation() = applicationScope.launch {
        locationsDao.getAllFromDb().collect { data ->
            allLocationData = data
        }
    }

    override suspend fun addLocation(location: LocationInfoRemote?) {
        if (location == null) return
        locationsDao.addLocation(mapper.transformLocationInfoRemoteIntoLocationInfoDto(location))
    }

    override suspend fun getAllLocations(
        pageIndex: Int,
        filter: Array<String?>
    ): AllLocationsResponse? {
        Log.d("SSV_REP_LOC", "local size = ${allLocationData.size}")
        val filtered = allLocationData.filter { dto ->
            utils.filteringItem(filter[0], dto.locationName)
                    && utils.filteringItem(filter[1], dto.locationType)
                    && utils.filteringItem(filter[2], dto.locationDimension)
        }
        if (filtered.isEmpty()) return null
        val filteredItemsPage = filtered
            .take(pageIndex * PAGE_SIZE)
            .drop((pageIndex - 1) * PAGE_SIZE)
            .map { mapper.transformLocationInfoDtoIntoLocationInfoRemote(it) }
        if (filteredItemsPage.isEmpty()) return null
        val countPages = filtered.size / 20 + if (filtered.size % 20 != 0) 1 else 0
        val prevPage = if (pageIndex > 1) utils.getPageString(pageIndex - 1) else null
        val nextPage = if (pageIndex == countPages) null else utils.getPageString(pageIndex + 1)
        val pageInfoResponse = PageInfoResponse(filtered.size, countPages, nextPage, prevPage)
        return AllLocationsResponse(pageInfoResponse, filteredItemsPage)
    }

    override fun getSingleLocationInfoRx(id: Int): Single<LocationInfoDto> {
        return locationsDao.getSingleLocationRx(id)
    }

    override suspend fun getLocationInfo(id: Int): LocationInfoRemote? {
        val localData = locationsDao.getSingleLocation(id) ?: return null
        return mapper.transformLocationInfoDtoIntoLocationInfoRemote(localData)
    }

    override fun getCountOfLocations(filters: Array<String?>): Single<Int> {
        val count = allLocationData.count {
            utils.filteringItem(filters[0], it.locationName) &&
                    utils.filteringItem(filters[1], it.locationType) &&
                    utils.filteringItem(filters[2], it.locationDimension)
        }
        return Single.just(count)
    }
}