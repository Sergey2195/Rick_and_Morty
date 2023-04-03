package com.aston.rickandmorty.data.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aston.rickandmorty.data.mappers.EpisodesMapper
import com.aston.rickandmorty.data.remoteDataSource.models.AllEpisodesResponse
import com.aston.rickandmorty.domain.entity.EpisodeModel
import com.aston.rickandmorty.utils.Utils

class EpisodesPagingSource(
    private val mapper: EpisodesMapper,
    private val utils: Utils,
    private val loader: suspend (pageIndex: Int) -> AllEpisodesResponse?
) : PagingSource<Int, EpisodeModel>() {

    override fun getRefreshKey(state: PagingState<Int, EpisodeModel>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EpisodeModel> {
        val pageIndex = params.key ?: START_PAG
        return try {
            val response = loader.invoke(pageIndex) ?: throw Exception("EpisodesPagingSource")
            val resultData = response.listEpisodeInfo ?: throw Exception("EpisodesPagingSource")
            val prevPage = utils.findPage(response.pageInfo?.prevPageUrl)
            val nextPage = utils.findPage(response.pageInfo?.nextPageUrl)
            val mappedList = mapper.transformListEpisodeInfoRemoteIntoListEpisodeModel(resultData)
            return LoadResult.Page(mappedList, prevPage, nextPage)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val START_PAG = 1
    }

}