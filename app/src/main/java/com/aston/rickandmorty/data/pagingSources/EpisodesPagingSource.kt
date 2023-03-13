package com.aston.rickandmorty.data.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aston.rickandmorty.data.models.AllEpisodesResponse
import com.aston.rickandmorty.domain.entity.EpisodeModel
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.utils.Utils
import java.io.IOException

class EpisodesPagingSource(private val loader: suspend (pageIndex: Int) -> AllEpisodesResponse) :
    PagingSource<Int, EpisodeModel>() {
    override fun getRefreshKey(state: PagingState<Int, EpisodeModel>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EpisodeModel> {
        val pageIndex = params.key ?: START_PAG
        return try {
            val response = loader.invoke(pageIndex)
            val resultData = response.listEpisodeInfo ?: throw IOException()
            val prevPage = Utils.findPage(response.pageInfo?.prevPageUrl)
            val nextPage = Utils.findPage(response.pageInfo?.nextPageUrl)
            val mappedList = Mapper.transformListEpisodeInfoRemoteIntoListEpisodeModel(resultData)
            return LoadResult.Page(mappedList, prevPage, nextPage)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val START_PAG = 1
    }

}