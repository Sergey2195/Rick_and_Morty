package com.aston.rickandmorty.data.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aston.rickandmorty.data.apiCalls.ApiCall
import com.aston.rickandmorty.domain.entity.EpisodeModel
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.utils.Utils
import java.io.IOException

class EpisodesPagingSource(private val apiCall: ApiCall) : PagingSource<Int, EpisodeModel>() {
    override fun getRefreshKey(state: PagingState<Int, EpisodeModel>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EpisodeModel> {
        val pageIndex = params.key ?: START_PAG
        return try {
            val response = apiCall.getAllEpisodes(pageIndex)
            val resultData = response.listEpisodeInfo ?: throw IOException()
            val prevPage = Utils.getLastIntAfterEquals(response.pageInfo?.prevPageUrl)
            val nextPage = Utils.getLastIntAfterEquals(response.pageInfo?.nextPageUrl)
            val mappedList = Mapper.transformListEpisodeInfoRemoteIntoListEpisodeModel(resultData)
            return LoadResult.Page(mappedList, prevPage, nextPage)
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val START_PAG = 1
    }

}