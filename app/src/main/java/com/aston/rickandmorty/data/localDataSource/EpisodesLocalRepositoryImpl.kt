package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.localDataSource.LocalRepositoriesUtils.Companion.PAGE_SIZE
import com.aston.rickandmorty.data.localDataSource.dao.EpisodesDao
import com.aston.rickandmorty.data.localDataSource.models.EpisodeInfoDto
import com.aston.rickandmorty.data.mappers.Mapper
import com.aston.rickandmorty.data.remoteDataSource.models.AllEpisodesResponse
import com.aston.rickandmorty.data.remoteDataSource.models.EpisodeInfoRemote
import com.aston.rickandmorty.data.remoteDataSource.models.PageInfoResponse
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EpisodesLocalRepositoryImpl @Inject constructor(
    private val mapper: Mapper,
    private val episodesDao: EpisodesDao,
    private val applicationScope: CoroutineScope,
    private val utils: LocalRepositoriesUtils
) : EpisodesLocalRepository {

    private var allEpisodesData: List<EpisodeInfoDto> = emptyList()

    init {
        collectEpisodesLocalItems()
    }

    override suspend fun getAllEpisodes(
        pageIndex: Int,
        filters: Array<String?>
    ): AllEpisodesResponse? {
        val filtered = allEpisodesData.filter { dto ->
            utils.filteringItem(filters[0], dto.episodeName)
                    && utils.filteringItem(filters[1], dto.episodeNumber)
        }
        if (filtered.isEmpty()) return null
        val filteredItemsPage = filtered
            .take(pageIndex * PAGE_SIZE)
            .drop((pageIndex - 1) * PAGE_SIZE)
            .map { mapper.transformEpisodeInfoDtoIntoEpisodeInfoRemote(it) }
        if (filteredItemsPage.isEmpty()) return null
        val countPages = filtered.size / 20 + if (filtered.size % 20 != 0) 1 else 0
        val prevPage = if (pageIndex > 1) utils.getPageString(pageIndex - 1) else null
        val nextPage = if (pageIndex == countPages) null else utils.getPageString(pageIndex + 1)
        val pageInfoResponse = PageInfoResponse(filtered.size, countPages, nextPage, prevPage)
        return AllEpisodesResponse(pageInfoResponse, filteredItemsPage)
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
        val count = allEpisodesData.filter {
            utils.filteringItem(filters[0], it.episodeName) && utils.filteringItem(
                filters[1],
                it.episodeNumber
            )
        }.size
        return Single.just(count)
    }

    private fun collectEpisodesLocalItems() = applicationScope.launch(Dispatchers.IO) {
        episodesDao.getAllFromDb().collect { list ->
            allEpisodesData = list.sortedBy { it.episodeId }
        }
    }
}