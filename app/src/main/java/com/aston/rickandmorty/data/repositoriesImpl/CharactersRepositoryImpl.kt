package com.aston.rickandmorty.data.repositoriesImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aston.rickandmorty.data.apiCalls.CharactersApiCall
import com.aston.rickandmorty.data.localDataSource.CharactersLocalRepository
import com.aston.rickandmorty.data.mappers.CharactersMapper
import com.aston.rickandmorty.data.pagingSources.CharactersPagingSource
import com.aston.rickandmorty.data.remoteDataSource.CharactersRemoteRepository
import com.aston.rickandmorty.data.remoteDataSource.models.AllCharactersResponse
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.repository.CharactersRepository
import com.aston.rickandmorty.domain.repository.SharedRepository
import com.aston.rickandmorty.utils.Utils
import io.reactivex.Single
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ApplicationScope
class CharactersRepositoryImpl @Inject constructor(
    private val charactersApiCall: CharactersApiCall,
    private val mapper: CharactersMapper,
    private val utils: Utils,
    private val pagingConfig: PagingConfig,
    private val remoteRepository: CharactersRemoteRepository,
    private val localRepository: CharactersLocalRepository,
    private val applicationScope: CoroutineScope,
    private val sharedRepository: SharedRepository
) : CharactersRepository {

    private var isNotFullData = false

    override fun getFlowAllCharacters(
        filters: Array<String?>,
        forceUpdate: Boolean
    ): Flow<PagingData<CharacterModel>> {
        return getCharactersPager(filters, forceUpdate).flow
    }

    private fun getCharactersPager(
        filters: Array<String?>,
        forceUpdate: Boolean
    ): Pager<Int, CharacterModel> {
        return Pager(pagingConfig) {
            CharactersPagingSource(mapper, utils) { pageIndex ->
                loadCharacters(pageIndex, filters, forceUpdate)
            }
        }
    }

    private suspend fun checkFirstPage(
        localItems: AllCharactersResponse?,
        filters: Array<String?>
    ) {
        val remoteItems = remoteRepository.getAllCharacters(1, filters)
        checkIsNotFullData(
            localItems?.pageInfo?.countOfElements,
            remoteItems?.pageInfo?.countOfElements
        )
    }

    private suspend fun loadCharacters(
        pageIndex: Int,
        filters: Array<String?>,
        forceUpdate: Boolean
    ): AllCharactersResponse? = withContext(Dispatchers.IO) {
        setLoading(true)
        if (forceUpdate) return@withContext downloadAndUpdateCharactersData(pageIndex, filters)
        val localItems = localRepository.getAllCharacters(pageIndex, filters)
        if (pageIndex == 1) {
            checkFirstPage(localItems, filters)
        }
        if (isNotFullData) {
            val remoteData = downloadAndUpdateCharactersData(pageIndex, filters)
            remoteData ?: localItems
        } else {
            localItems
        }
    }.also { setLoading(false) }

    private fun checkIsNotFullData(localItems: Int?, remoteItems: Int?) {
        isNotFullData = (localItems ?: -1) < (remoteItems ?: -1)
    }

    private suspend fun downloadAndUpdateCharactersData(
        pageIndex: Int,
        filters: Array<String?>
    ): AllCharactersResponse? = withContext(Dispatchers.IO) {
        val networkResponse =
            remoteRepository.getAllCharacters(pageIndex, filters)
        if (networkResponse?.listCharactersInfo == null) return@withContext null
        for (character in networkResponse.listCharactersInfo) {
            applicationScope.launch {
                localRepository.addCharacter(character)
            }
        }
        return@withContext networkResponse
    }

    override suspend fun getCharacterData(id: Int, forceUpdate: Boolean): CharacterDetailsModel? =
        withContext(Dispatchers.IO) {
            if (forceUpdate) return@withContext downloadAndUpdateCharacterData(id)
            val localData = localRepository.getCharacterInfo(id)
                ?: return@withContext downloadAndUpdateCharacterData(id)
            mapper.transformCharacterInfoDtoIntoCharacterDetailsModel(localData)
        }

    override suspend fun getListCharactersData(
        listId: List<Int>,
        forceUpdate: Boolean
    ): List<CharacterModel> {
        setLoading(true)
        val listJobs = mutableListOf<Job>()
        val resultList = mutableListOf<CharacterModel>()
        for (id in listId) {
            val job = applicationScope.launch {
                val idData = getCharacterData(id, forceUpdate)
                val mappedData = mapper.transformCharacterDetailsModelIntoCharacterModel(idData)
                if (mappedData != null) {
                    resultList.add(mappedData)
                }
            }
            listJobs.add(job)
        }
        listJobs.joinAll()
        setLoading(false)
        return resultList
    }

    private suspend fun downloadAndUpdateCharacterData(id: Int): CharacterDetailsModel? =
        withContext(Dispatchers.IO) {
            val remoteData = remoteRepository.getSingleCharacterInfo(id)
            if (remoteData == null) {
                setLoadingChange()
                return@withContext null
            }
            localRepository.addCharacter(remoteData)
            return@withContext mapper.transformCharacterInfoRemoteIntoCharacterDetailsModel(
                remoteData
            )
        }

    private suspend fun setLoadingChange() {
        setLoading(true)
        delay(100)
        setLoading(false)
    }

    override fun getCountOfCharacters(filters: Array<String?>): Single<Int> {
        return remoteRepository.getCountOfCharacters(filters).onErrorResumeNext {
            localRepository.getCountOfCharacters(filters)
        }
    }

    override suspend fun getMultiCharacterModelOnlyRemote(multiId: String): List<CharacterModel> =
        withContext(Dispatchers.IO) {
            return@withContext mapper.transformListCharacterInfoRemoteIntoCharacterModel(
                charactersApiCall.getMultiCharactersData(multiId)
            )
        }

    private fun setLoading(isLoading: Boolean) {
        sharedRepository.setLoadingProgressStateFlow(isLoading)
    }
}