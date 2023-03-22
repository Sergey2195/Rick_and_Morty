package com.aston.rickandmorty.data.repositoriesImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aston.rickandmorty.data.apiCalls.CharactersApiCall
import com.aston.rickandmorty.data.localDataSource.CharactersLocalRepository
import com.aston.rickandmorty.data.mappers.Mapper
import com.aston.rickandmorty.data.pagingSources.CharactersPagingSource
import com.aston.rickandmorty.data.remoteDataSource.CharactersRemoteRepository
import com.aston.rickandmorty.data.remoteDataSource.models.AllCharactersResponse
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.repository.CharactersRepository
import com.aston.rickandmorty.domain.repository.SharedRepository
import com.aston.rickandmorty.utils.Utils
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CharactersRepositoryImpl @Inject constructor(
    private val charactersApiCall: CharactersApiCall,
    private val mapper: Mapper,
    private val utils: Utils,
    private val pagingConfig: PagingConfig,
    private val sharedRepository: SharedRepository,
    private val remoteRepository: CharactersRemoteRepository,
    private val localRepository: CharactersLocalRepository,
    private val applicationScope: CoroutineScope
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

    private suspend fun loadCharacters(
        pageIndex: Int,
        filters: Array<String?>,
        forceUpdate: Boolean
    ): AllCharactersResponse? = withContext(Dispatchers.IO) {
        setLoading(true)
        if (forceUpdate) return@withContext downloadAndUpdateCharactersData(pageIndex, filters)
        val localItems = localRepository.getAllCharacters(pageIndex, filters)
        if (pageIndex == 1) {
            val remoteItems = remoteRepository.getAllCharacters(pageIndex, filters)
            checkIsNotFullData(
                localItems?.pageInfo?.countOfElements,
                remoteItems?.pageInfo?.countOfElements
            )
        }
        return@withContext if (isNotFullData) {
            downloadAndUpdateCharactersData(pageIndex, filters)
        } else {
            localItems
        }
    }.also {
        setLoading(false)
    }

    private fun checkIsNotFullData(localItems: Int?, remoteItems: Int?) {
        isNotFullData = (localItems ?: -1) < (remoteItems ?: -1)
    }

    private suspend fun downloadAndUpdateCharactersData(
        pageIndex: Int,
        filters: Array<String?>
    ): AllCharactersResponse? = withContext(Dispatchers.IO) {
        setLoading(true)
        val networkResponse =
            remoteRepository.getAllCharacters(pageIndex, filters)
        if (networkResponse?.listCharactersInfo == null) return@withContext null
        for (character in networkResponse.listCharactersInfo) {
            applicationScope.launch {
                localRepository.addCharacter(character)
            }
        }
        return@withContext networkResponse
    }.also { setLoading(false) }

    override suspend fun getCharacterData(id: Int, forceUpdate: Boolean): CharacterDetailsModel? =
        withContext(Dispatchers.IO) {
            setLoading(true)
            if (forceUpdate) return@withContext downloadAndUpdateCharacterData(id)
            val localData = localRepository.getCharacterInfo(id)
                ?: return@withContext downloadAndUpdateCharacterData(id)
            mapper.transformCharacterInfoDtoIntoCharacterDetailsModel(localData)
        }.also { setLoading(false) }

    private suspend fun downloadAndUpdateCharacterData(id: Int): CharacterDetailsModel? =
        withContext(Dispatchers.IO) {
            val remoteData = remoteRepository.getSingleCharacterInfo(id) ?: return@withContext null
            localRepository.addCharacter(remoteData)
            return@withContext mapper.transformCharacterInfoRemoteIntoCharacterDetailsModel(
                remoteData
            )
        }

    override fun getCountOfCharacters(filters: Array<String?>): Single<Int> {
        return remoteRepository.getCountOfCharacters(filters).onErrorResumeNext {
            localRepository.getCountOfCharacters(filters)
        }
    }

    override suspend fun getMultiCharacterModel(multiId: String): List<CharacterModel> =
        withContext(Dispatchers.IO) {
            //todo
            return@withContext mapper.transformListCharacterInfoRemoteIntoCharacterModel(
                charactersApiCall.getMultiCharactersData(multiId)
            )
        }

    private fun setLoading(isLoading: Boolean) {
        sharedRepository.setLoadingProgressStateFlow(isLoading)
    }
}