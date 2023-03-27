package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.localDataSource.dao.EpisodesDao
import com.aston.rickandmorty.data.localDataSource.models.EpisodeInfoDto
import com.aston.rickandmorty.data.mappers.EpisodesMapper
import com.aston.rickandmorty.data.remoteDataSource.models.AllEpisodesResponse
import com.aston.rickandmorty.data.remoteDataSource.models.EpisodeInfoRemote
import com.aston.rickandmorty.data.remoteDataSource.models.PageInfoResponse
import com.aston.rickandmorty.di.ApplicationScope
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ApplicationScope
class EpisodesLocalRepositoryImpl @Inject constructor(
    private val mapper: EpisodesMapper,
    private val episodesDao: EpisodesDao,
    private val applicationScope: CoroutineScope,
    private val utils: LocalRepositoriesUtils,
    private val pageSize: Int
) : EpisodesLocalRepository {

    private var allEpisodesData: List<EpisodeInfoDto> = emptyList()

    init {
        collectEpisodesLocalItems()
    }

    override suspend fun getAllEpisodes(
        pageIndex: Int,
        filters: Array<String?>
    ): AllEpisodesResponse? {
        val filtered = filteringEpisodes(filters)
        if (filtered.isEmpty()) return null
        val filteredItemsPage = takePage(filtered, pageIndex)
        if (filteredItemsPage.isEmpty()) return null
        val pageInfoResponse = pageInfo(filtered, pageIndex)
        return AllEpisodesResponse(pageInfoResponse, filteredItemsPage)
    }

    private fun filteringEpisodes(filters: Array<String?>): List<EpisodeInfoDto> {
        return allEpisodesData.filter { filter(filters, it) }
    }

    private fun takePage(filtered: List<EpisodeInfoDto>, pageIndex: Int): List<EpisodeInfoRemote> {
        return filtered
            .take(pageIndex * pageSize)
            .drop((pageIndex - 1) * pageSize)
            .map { mapper.transformEpisodeInfoDtoIntoEpisodeInfoRemote(it) }
    }

    private fun pageInfo(filtered: List<EpisodeInfoDto>, pageIndex: Int): PageInfoResponse {
        val countPages = filtered.size / pageSize + if (filtered.size % pageSize != 0) 1 else 0
        val prevPage = if (pageIndex > 1) utils.getPageString(pageIndex - 1) else null
        val nextPage = if (pageIndex == countPages) null else utils.getPageString(pageIndex + 1)
        return PageInfoResponse(filtered.size, countPages, nextPage, prevPage)
    }

    override suspend fun addEpisode(data: EpisodeInfoRemote?) = withContext(Dispatchers.IO) {
        if (data == null) return@withContext
        val mappedItem = mapper.transformEpisodeInfoRemoteIntoEpisodeInfoDto(data)
        episodesDao.addEpisode(mappedItem)
    }

    override suspend fun getEpisodeData(id: Int): EpisodeInfoRemote? = withContext(Dispatchers.IO) {
        val episodeData = episodesDao.getEpisode(id) ?: return@withContext null
        return@withContext mapper.transformEpisodeInfoDtoIntoEpisodeInfoRemote(episodeData)
    }

    override fun getCountOfEpisodes(filters: Array<String?>): Single<Int> {
        val count = allEpisodesData.count { filter(filters, it) }
        return Single.just(if (count == 0) -1 else count)
    }

    private fun collectEpisodesLocalItems() = applicationScope.launch(Dispatchers.IO) {
        episodesDao.getAllFromDb().collect { list ->
            allEpisodesData = list.sortedBy { it.episodeId }
        }
    }

    private fun filter(filters: Array<String?>, dto: EpisodeInfoDto): Boolean {
        return utils.filteringItem(filters[0], dto.episodeName)
                && utils.filteringItem(filters[1], dto.episodeNumber)
    }
}