package com.aston.rickandmorty.data.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aston.rickandmorty.data.models.AllLocationsResponse
import com.aston.rickandmorty.domain.entity.LocationModel
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.utils.Utils
import retrofit2.HttpException
import java.io.IOException

class LocationsPagingSource(private val loader: suspend (pageIndex: Int)-> AllLocationsResponse?) :
    PagingSource<Int, LocationModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LocationModel> {
        val pageIndex = params.key ?: START_PAGE
        return try {
            val response = loader.invoke(pageIndex)
            val resultData = response?.listLocationsInfo ?: return LoadResult.Error(Exception())
            val prevPage = Utils.findPage(response.pageInfo?.prevPageUrl)
            val nextPage = Utils.findPage(response.pageInfo?.nextPageUrl)
            val mappedList = Mapper.transformListLocationInfoRemoteIntoListLocationModel(resultData)
            LoadResult.Page(mappedList, prevPage, nextPage)
        }catch (e:Exception){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LocationModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    companion object {
        private const val START_PAGE = 1
    }
}