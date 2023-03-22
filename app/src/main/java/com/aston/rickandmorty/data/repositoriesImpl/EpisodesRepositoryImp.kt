package com.aston.rickandmorty.data.repositoriesImpl

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aston.rickandmorty.data.localDataSource.EpisodesLocalRepository
import com.aston.rickandmorty.data.models.AllEpisodesResponse
import com.aston.rickandmorty.data.pagingSources.EpisodesPagingSource
import com.aston.rickandmorty.data.remoteDataSource.EpisodesRemoteRepository
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.entity.EpisodeDetailsModel
import com.aston.rickandmorty.domain.entity.EpisodeModel
import com.aston.rickandmorty.domain.repository.CharactersRepository
import com.aston.rickandmorty.domain.repository.EpisodesRepository
import com.aston.rickandmorty.domain.repository.SharedRepository
import com.aston.rickandmorty.mappers.Mapper
import io.reactivex.Single
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EpisodesRepositoryImp @Inject constructor(
    private val sharedRepository: SharedRepository,
    private val mapper: Mapper,
    private val applicationScope: CoroutineScope,
    private val episodesLocalRepository: EpisodesLocalRepository,
    private val episodesRemoteRepository: EpisodesRemoteRepository,
    private val charactersRepository: CharactersRepository,
    private val pagingConfig: PagingConfig
) : EpisodesRepository {

    private var isNotFullData = false

    override fun getFlowAllEpisodes(
        filters: Array<String?>,
        forceUpdate: Boolean
    ): Flow<PagingData<EpisodeModel>> {
        return getEpisodesPager(filters, forceUpdate).flow
    }

    override suspend fun getEpisodeData(id: Int, forceUpdate: Boolean): EpisodeDetailsModel? =
        withContext(Dispatchers.IO) {
            if (forceUpdate) return@withContext downloadAndUpdateEpisodeData(id)
            setLoading(true)
            val localResult = episodesLocalRepository.getEpisodeData(id)
                ?: return@withContext downloadAndUpdateEpisodeData(id)
            val charactersModel = getCharactersModel(localResult.episodeCharacters)
            setLoading(false)
            mapper.configurationEpisodeDetailsModel(localResult, charactersModel)
        }

    private suspend fun getCharactersModel(listIds: List<String>?): List<CharacterModel> {
        if (listIds == null) return emptyList()
        val listJob = arrayListOf<Job>()
        val resultList = arrayListOf<CharacterModel>()
        for (id in listIds) {
            val job = applicationScope.launch {
                val preparedId = mapper.transformIdWithStringAndSlashIntoInt(id)
                val characterData =
                    charactersRepository.getCharacterData(preparedId, false) ?: return@launch
                val characterModel =
                    mapper.transformCharacterDetailsModelIntoCharacterModel(characterData)
                        ?: return@launch
                resultList.add(characterModel)
            }
            listJob.add(job)
        }
        listJob.joinAll()
        return resultList
    }

    override suspend fun getListEpisodeModel(
        multiId: String,
        forceUpdate: Boolean
    ): List<EpisodeModel>? = withContext(Dispatchers.IO) {
        setLoading(true)
        val listId = multiId.split(",").map { it.toInt() }
        if (listId.isEmpty()) return@withContext null
        val listJob = arrayListOf<Job>()
        val resultList = arrayListOf<EpisodeModel>()
        for (id in listId) {
            val job = applicationScope.launch {
                val data = getEpisodeDataWithoutCharacters(id, forceUpdate)
                if (data != null) resultList.add(data)
            }
            listJob.add(job)
        }
        listJob.joinAll()
        sortedList(resultList)
    }

    private suspend fun getEpisodeDataWithoutCharacters(
        id: Int,
        forceUpdate: Boolean
    ): EpisodeModel? {
        setLoading(true)
        if (forceUpdate) return downloadEpisodeDetails(id)
        var resultData = episodesLocalRepository.getEpisodeData(id)
        if (resultData == null){
            val remoteData = episodesRemoteRepository.getEpisodeInfo(id)
            episodesLocalRepository.addEpisode(remoteData)
            resultData = remoteData
        }
        setLoading(false)
        return mapper.transformEpisodeInfoRemoteIntoEpisodeModel(resultData ?: return null)
    }

    private suspend fun downloadEpisodeDetails(id: Int): EpisodeModel? =
        withContext(Dispatchers.IO) {
            val remoteData = episodesRemoteRepository.getEpisodeInfo(id) ?: return@withContext null
            episodesLocalRepository.addEpisode(remoteData)
            return@withContext mapper.transformEpisodeInfoRemoteIntoEpisodeModel(remoteData)
        }.also { setLoading(false) }

    override fun getCountOfEpisodes(filters: Array<String?>): Single<Int> {
        return if (sharedRepository.getStateFlowIsConnected().value) {
            episodesRemoteRepository.getCountOfEpisodes(filters).onErrorResumeNext {
                episodesLocalRepository.getCountOfEpisodes(filters)
            }
        } else {
            episodesLocalRepository.getCountOfEpisodes(filters)
        }
    }

    private fun getEpisodesPager(
        filters: Array<String?>,
        forceUpdate: Boolean
    ): Pager<Int, EpisodeModel> {
        return Pager(pagingConfig) {
            EpisodesPagingSource(mapper) { pageIndex ->
                loadEpisodes(
                    pageIndex,
                    filters,
                    forceUpdate
                )
            }
        }
    }

    private suspend fun loadEpisodes(
        pageIndex: Int,
        filters: Array<String?>,
        forceUpdate: Boolean
    ): AllEpisodesResponse? = withContext(Dispatchers.IO) {
        setLoading(true)
        if (forceUpdate) return@withContext downloadAndUpdateEpisodesData(pageIndex, filters)
        val localItems = episodesLocalRepository.getAllEpisodes(pageIndex, filters)
        Log.d("SSV_REP", "$localItems")
        if (pageIndex == 1) {
            val remoteItems = episodesRemoteRepository.getAllEpisodes(1, filters)
            checkIsNotFullData(
                localItems?.pageInfo?.countOfElements,
                remoteItems?.pageInfo?.countOfElements
            )
        }
        return@withContext if (isNotFullData) {
            downloadAndUpdateEpisodesData(pageIndex, filters)
        } else {
            localItems
        }
    }.also { setLoading(false) }

    private fun checkIsNotFullData(localItems: Int?, remoteItems: Int?) {
        isNotFullData = (localItems ?: -1) < (remoteItems ?: -1)
    }

    private suspend fun downloadAndUpdateEpisodesData(
        pageIndex: Int,
        filters: Array<String?>
    ): AllEpisodesResponse? = withContext(Dispatchers.IO) {
        val networkResponse = episodesRemoteRepository.getAllEpisodes(pageIndex, filters)
        if (networkResponse?.listEpisodeInfo == null) {
            return@withContext null
        }
        for (episode in networkResponse.listEpisodeInfo) {
            applicationScope.launch {
                episodesLocalRepository.addEpisode(episode)
            }
        }
        networkResponse
    }

    private suspend fun downloadAndUpdateEpisodeData(id: Int): EpisodeDetailsModel? =
        withContext(Dispatchers.IO) {
            setLoading(true)
            val remoteData = episodesRemoteRepository.getEpisodeInfo(id) ?: return@withContext null
            episodesLocalRepository.addEpisode(remoteData)
            val multiIdString =
                mapper.transformListStringIdToStringWithoutSlash(remoteData.episodeCharacters) ?: ""
            val charactersModel =
                charactersRepository.getMultiCharacterModel(multiIdString)
            return@withContext mapper.configurationEpisodeDetailsModel(remoteData, charactersModel)
                .also {
                    setLoading(false)
                }
        }.also { setLoading(false) }

    private fun setLoading(isLoading: Boolean) {
        sharedRepository.setLoadingProgressStateFlow(isLoading)
    }

    private fun sortedList(list: List<EpisodeModel>): List<EpisodeModel> {
        return try {
            list.sortedBy { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }
}