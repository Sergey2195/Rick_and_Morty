package com.aston.rickandmorty.data.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aston.rickandmorty.data.models.AllLocationsResponse
import com.aston.rickandmorty.domain.entity.LocationModel
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.utils.Utils
import retrofit2.HttpException
import java.io.IOException

class LocationsPagingSource(private val loader: suspend (pageIndex: Int)-> AllLocationsResponse) :
    PagingSource<Int, LocationModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LocationModel> {
        val pageIndex = params.key ?: START_PAGE
        return try {
            val response = loader.invoke(pageIndex)
            val resultData = response.listLocationsInfo ?: throw IOException()
            val prevPage = Utils.findPage(response.pageInfo?.prevPageUrl)
            val nextPage = Utils.findPage(response.pageInfo?.nextPageUrl)
            val mappedList = Mapper.transformListLocationInfoRemoteIntoListLocationModel(resultData)
            LoadResult.Page(mappedList, prevPage, nextPage)
        }catch (e:Exception){
            if (e is HttpException && e.code() == 404) {
                return LoadResult.Page(emptyList(), pageIndex - 1, null)
            }
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LocationModel>): Int? {
        return null
    }

    companion object {
        private const val START_PAGE = 1
    }
}