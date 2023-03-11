package com.aston.rickandmorty.data.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aston.rickandmorty.data.apiCalls.ApiCall
import com.aston.rickandmorty.domain.entity.LocationModel
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.utils.Utils
import java.io.IOException

class LocationsPagingSource(private val apiCall: ApiCall) :
    PagingSource<Int, LocationModel>() {
    override fun getRefreshKey(state: PagingState<Int, LocationModel>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LocationModel> {
        val pageIndex = params.key ?: START_PAGE
        return try {
            val response = apiCall.getAllLocations(pageIndex)
            val resultData = response.listLocationsInfo ?: throw IOException()
            val prevPage = Utils.findPage(response.pageInfo?.prevPageUrl)
            val nextPage = Utils.findPage(response.pageInfo?.nextPageUrl)
            val mappedList = Mapper.transformListLocationInfoRemoteIntoListLocationModel(resultData)
            LoadResult.Page(mappedList, prevPage, nextPage)
        }catch (e:Exception){
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val START_PAGE = 1
    }
}