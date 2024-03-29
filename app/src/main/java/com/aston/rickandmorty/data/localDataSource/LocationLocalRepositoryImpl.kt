package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.localDataSource.dao.LocationsDao
import com.aston.rickandmorty.data.localDataSource.models.LocationInfoDto
import com.aston.rickandmorty.data.mappers.LocationsMapper
import com.aston.rickandmorty.data.remoteDataSource.models.AllLocationsResponse
import com.aston.rickandmorty.data.remoteDataSource.models.LocationInfoRemote
import com.aston.rickandmorty.data.remoteDataSource.models.PageInfoResponse
import com.aston.rickandmorty.di.ApplicationScope
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@ApplicationScope
class LocationLocalRepositoryImpl @Inject constructor(
    private val mapper: LocationsMapper,
    private val locationsDao: LocationsDao,
    private val applicationScope: CoroutineScope,
    private val utils: LocalRepositoriesUtils,
    private val pageSize: Int
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
        val filtered = filteringLocations(filter)
        if (filtered.isEmpty()) return null
        val filteredItemsPage = takePage(filtered, pageIndex)
        if (filteredItemsPage.isEmpty()) return null
        val pageInfoResponse = pageInfo(filtered, pageIndex)
        return AllLocationsResponse(pageInfoResponse, filteredItemsPage)
    }

    private fun filteringLocations(filter: Array<String?>): List<LocationInfoDto> {
        return allLocationData.filter { filtering(filter, it) }
    }

    private fun pageInfo(filtered: List<LocationInfoDto>, pageIndex: Int): PageInfoResponse {
        val countPages = filtered.size / pageSize + if (filtered.size % pageSize != 0) 1 else 0
        val prevPage = if (pageIndex > 1) utils.getPageString(pageIndex - 1) else null
        val nextPage = if (pageIndex == countPages) null else utils.getPageString(pageIndex + 1)
        return PageInfoResponse(filtered.size, countPages, nextPage, prevPage)
    }

    override fun getSingleLocationInfoRx(id: Int): Single<LocationInfoDto> {
        return locationsDao.getSingleLocationRx(id)
    }

    override suspend fun getLocationInfo(id: Int): LocationInfoRemote? {
        val localData = locationsDao.getSingleLocation(id) ?: return null
        return mapper.transformLocationInfoDtoIntoLocationInfoRemote(localData)
    }

    override fun getCountOfLocations(filters: Array<String?>): Single<Int> {
        val count = allLocationData.count { filtering(filters, it) }
        return Single.just(if (count == 0) -1 else count)
    }

    private fun filtering(filters: Array<String?>, dto: LocationInfoDto): Boolean {
        return utils.filteringItem(filters[0], dto.locationName) &&
                utils.filteringItem(filters[1], dto.locationType) &&
                utils.filteringItem(filters[2], dto.locationDimension)
    }

    private fun takePage(
        filtered: List<LocationInfoDto>,
        pageIndex: Int
    ): List<LocationInfoRemote> {
        return filtered
            .take(pageIndex * pageSize)
            .drop((pageIndex - 1) * pageSize)
            .map { mapper.transformLocationInfoDtoIntoLocationInfoRemote(it) }
    }
}