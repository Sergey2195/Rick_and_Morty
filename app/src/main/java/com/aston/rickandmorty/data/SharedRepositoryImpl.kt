package com.aston.rickandmorty.data

import android.app.Application
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aston.rickandmorty.data.apiCalls.ApiCall
import com.aston.rickandmorty.data.localDataSource.LocalRepository
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.AllEpisodesResponse
import com.aston.rickandmorty.data.models.AllLocationsResponse
import com.aston.rickandmorty.data.models.EpisodeInfoRemote
import com.aston.rickandmorty.data.pagingSources.CharactersPagingSource
import com.aston.rickandmorty.data.pagingSources.EpisodesPagingSource
import com.aston.rickandmorty.data.pagingSources.LocationsPagingSource
import com.aston.rickandmorty.data.remoteDataSource.RemoteRepository
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.entity.*
import com.aston.rickandmorty.domain.repository.Repository
import com.aston.rickandmorty.mappers.Mapper
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ApplicationScope
class SharedRepositoryImpl @Inject constructor(
    application: Application,
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val apiCall: ApiCall,
    private val mapper: Mapper
) : Repository {

    private val loadingProgressStateFlow = MutableStateFlow(false)
    private var invalidData = false
    private val connectivityObserver = NetworkConnectivityObserver(application)
    private val connectionStatusIsAvailable =
        MutableStateFlow(connectivityObserver.isDeviceOnline(application.applicationContext))

    init {
        CoroutineScope(Dispatchers.IO).launch {
            connectivityObserver.observe().collect {
                connectionStatusIsAvailable.value = it == ConnectivityObserver.Status.Available
            }
        }
    }

    override fun getLoadingProgressStateFlow(): StateFlow<Boolean> {
        return loadingProgressStateFlow.asStateFlow()
    }

    override fun setLoadingProgressStateFlow(isLoading: Boolean) {
        loadingProgressStateFlow.value = isLoading
    }


    private fun setLoading(isLoading: Boolean) {
        Log.d("SSV_LOAD", "isLoading = $isLoading")
        loadingProgressStateFlow.value = isLoading
    }

    override fun getStateFlowIsConnected(): StateFlow<Boolean> {
        return connectionStatusIsAvailable.asStateFlow()
    }

    override fun getFlowAllCharacters(
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): Flow<PagingData<CharacterModel>> {
        val arrayFilters =
            arrayOf(nameFilter, statusFilter, speciesFilter, typeFilter, genderFilter)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = {
                CharactersPagingSource(mapper) { pageIndex ->
                    loadCharacters(
                        pageIndex,
                        arrayFilters
                    )
                }
            }
        ).flow
    }

    override fun getFlowAllLocations(
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?,
        forceUpdate: Boolean
    ): Flow<PagingData<LocationModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = {
                LocationsPagingSource(mapper) { pageIndex ->
                    loadLocations(pageIndex, nameFilter, typeFilter, dimensionFilter, forceUpdate)
                }
            }
        ).flow
    }

    override fun getFlowAllEpisodes(
        nameFilter: String?,
        episodeFilter: String?,
        forceUpdate: Boolean
    ): Flow<PagingData<EpisodeModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = {
                EpisodesPagingSource(mapper) { pageIndex ->
                    loadEpisodes(pageIndex, nameFilter, episodeFilter, forceUpdate)
                }
            }
        ).flow
    }

    private fun checkNotAllData(localItems: Int?, remoteItems: Int?) {
        invalidData = (localItems ?: -1) < (remoteItems ?: -1)
    }

    private suspend fun loadEpisodes(
        pageIndex: Int,
        nameFilter: String?,
        episodeFilter: String?,
        forceUpdate: Boolean
    ): AllEpisodesResponse {
        setLoading(true)
        if (forceUpdate) {
            return remoteResponseEpisodes(pageIndex, nameFilter, episodeFilter)
        }
        if (pageIndex == 1) {
            val localResponse = localRepository.getAllEpisodes(1, nameFilter, episodeFilter)
            val remoteResponse = remoteRepository.getAllEpisodes(1, nameFilter, episodeFilter)
            checkNotAllData(
                localResponse.pageInfo?.countOfElements,
                remoteResponse?.pageInfo?.countOfElements
            )
        }
        val resultResponse = if (invalidData) {
            val remoteResponse =
                remoteRepository.getAllEpisodes(pageIndex, nameFilter, episodeFilter)
            localRepository.writeResponseEpisodes(remoteResponse)
            remoteResponse
        } else {
            localRepository.getAllEpisodes(pageIndex, nameFilter, episodeFilter)
        }
        setLoading(false)
        return resultResponse ?: throw RuntimeException("loadEpisodes")
    }

    private suspend fun remoteResponseEpisodes(
        pageIndex: Int,
        nameFilter: String?,
        episodeFilter: String?
    ): AllEpisodesResponse {
        val remoteResponse = remoteRepository.getAllEpisodes(pageIndex, nameFilter, episodeFilter)
        if (remoteResponse == null) throw RuntimeException("loadEpisodes")
        localRepository.writeResponseEpisodes(remoteResponse)
        setLoading(false)
        return remoteResponse
    }

    private suspend fun loadCharacters(
        pageIndex: Int,
        arrayFilters: Array<String?>
    ): AllCharactersResponse {
        Log.d("SSV_R", "loadCharacters")
        setLoading(true)
        val localResponse = localRepository.getAllCharacters(pageIndex, arrayFilters)
        if (pageIndex == 1) {
            checkInvalidData(arrayFilters, localResponse)
        }
        val resultResponse = if (invalidData) {
            remoteRepository.getAllCharacters(
                pageIndex,
                arrayFilters
            ).also {
                localRepository.writeResponseCharacters(
                    it ?: throw RuntimeException("loadCharacters")
                )
            }
        } else {
            localResponse
        }
        setLoading(false)
        return resultResponse ?: throw RuntimeException("loadCharacters")
    }

    private suspend fun checkInvalidData(
        arrayFilters: Array<String?>,
        localResponse: AllCharactersResponse
    ) {
        val firstPage = remoteRepository.getAllCharacters(
            1,
            arrayFilters
        )
        val remoteItems = firstPage?.pageInfo?.countOfElements ?: -1
        val localItems = localResponse.pageInfo?.countOfElements ?: -1
        invalidData = localItems < remoteItems
    }

    private suspend fun checkInvalidDataLocations(
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?,
        localResponse: AllLocationsResponse
    ) {
        val firstPage = remoteRepository.getAllLocations(1, nameFilter, typeFilter, dimensionFilter)
        val remoteItems = firstPage?.pageInfo?.countOfElements ?: -1
        val localItems = localResponse.pageInfo?.countOfElements ?: -1
        invalidData = localItems < remoteItems
    }

    private suspend fun loadLocations(
        pageIndex: Int,
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?,
        forceUpdate: Boolean
    ): AllLocationsResponse? {
        setLoading(true)
        if (forceUpdate) {
            return remoteRepository.getAllLocations(
                pageIndex,
                nameFilter,
                typeFilter,
                dimensionFilter
            ).also {
                localRepository.writeResponseLocation(it)
                setLoading(false)
            }
        }
        val localResponse =
            localRepository.getAllLocations(pageIndex, nameFilter, typeFilter, dimensionFilter)
        if (pageIndex == 1) {
            checkInvalidDataLocations(nameFilter, typeFilter, dimensionFilter, localResponse)
        }
        val resultResponse = if (invalidData) {
            remoteRepository.getAllLocations(pageIndex, nameFilter, typeFilter, dimensionFilter)
                .also {
                    localRepository.writeResponseLocation(it)
                }
        } else {
            localResponse
        }
        setLoading(false)
        return resultResponse
    }

    override suspend fun getSingleCharacterData(
        id: Int,
        forceUpdate: Boolean
    ): CharacterDetailsModel? {
        return try {
            setLoading(true)
            if (forceUpdate) {
                return loadCharacterDataWithNetwork(id)
            }
            return localRepository.getSingleCharacterInfo(id) ?: loadCharacterDataWithNetwork(id)
        } catch (e: java.lang.Exception) {
            null
        } finally {
            setLoading(false)
        }
    }

    private suspend fun loadCharacterDataWithNetwork(id: Int): CharacterDetailsModel? {
        val remoteResult = remoteRepository.getSingleCharacterInfo(id)
        localRepository.writeSingleCharacterInfo(remoteResult ?: return null)
        return mapper.transformCharacterInfoRemoteIntoCharacterDetailsModel(remoteResult)
    }

    private fun getSingleLocationDataWithForceUpdate(id: Int): Single<LocationDetailsModelWithId> {
        return remoteRepository.getSingleLocationData(id)
            .map { mapper.transformLocationInfoRemoteInfoLocationDetailsModelWithIds(it) }
    }

    override fun getSingleLocationData(
        id: Int,
        forceUpdate: Boolean
    ): Single<LocationDetailsModelWithId> {
        if (forceUpdate) return getSingleLocationDataWithForceUpdate(id)
        return localRepository.getSingleLocationInfoRx(id).flatMap { data ->
            if (data.locationId == null) {
                return@flatMap apiCall.getSingleLocationData(id).map {
                    mapper.transformLocationInfoRemoteInfoLocationDetailsModelWithIds(it)
                }
            } else {
                Single.create {
                    it.onSuccess(mapper.transformLocationDtoIntoLocationDetailsWithIds(data))
                }
            }
        }
    }

    private suspend fun loadEpisodeDataWithNetwork(id: Int): EpisodeDetailsModel? {
        setLoading(true)
        val remote = remoteRepository.getSingleEpisodeInfo(id)
        localRepository.writeSingleEpisodeInfo(remote)
        val charactersString =
            mapper.transformListStringsToIds(remote?.episodeCharacters)?.replace("/", "")
        val characters = remoteRepository.getMultiIdCharacters(
            charactersString ?: throw RuntimeException("loadEpisodeDataWithNetwork")
        )
        if (characters != null) {
            for (char in characters) {
                localRepository.writeSingleCharacterInfo(char)
            }
        }
        return EpisodeDetailsModel(
            id = remote?.episodeId ?: return null,
            name = remote.episodeName ?: return null,
            airDate = remote.episodeAirDate ?: return null,
            episodeNumber = remote.episodeNumber ?: return null,
            characters = mapper.transformListCharacterInfoRemoteIntoCharacterModel(
                characters ?: throw RuntimeException("loadEpisodeDataWithNetwork")
            )
        ).also {
            setLoading(false)
        }
    }

    override suspend fun getSingleEpisodeData(id: Int, forceUpdate: Boolean): EpisodeDetailsModel? =
        withContext(Dispatchers.IO) {
            if (forceUpdate) {
                return@withContext loadEpisodeDataWithNetwork(id)
            }
            setLoading(true)
            val localResult = localRepository.getSingleEpisodeInfo(id)
            var remote: EpisodeInfoRemote? = null
            if (localResult == null) {
                remote = remoteRepository.getSingleEpisodeInfo(id)
                localRepository.writeSingleEpisodeInfo(remote)
            }
            val listId = if (localResult == null) {
                val charactersString =
                    mapper.transformListStringsToIds(remote?.episodeCharacters)
                mapper.transformStringIdIntoListInt(charactersString)
            } else {
                localResult.characters
            }
            val charactersData = arrayListOf<CharacterModel>()
            for (charId in listId) {
                val charData = getSingleCharacterData(charId, forceUpdate)
                val mappedData =
                    mapper.transformCharacterDetailsModelIntoCharacterModel(charData)
                        ?: continue
                if (charData != null) charactersData.add(mappedData)
            }
            return@withContext EpisodeDetailsModel(
                id = localResult?.id ?: remote?.episodeId ?: return@withContext null,
                name = localResult?.name ?: remote?.episodeName ?: return@withContext null,
                airDate = localResult?.airDate ?: remote?.episodeAirDate ?: return@withContext null,
                episodeNumber = localResult?.episodeNumber ?: remote?.episodeNumber
                ?: return@withContext null,
                characters = charactersData
            ).also {
                setLoading(false)
            }
        }


    override suspend fun getLocationModel(id: Int, forceUpdate: Boolean): LocationModel? {
        return try {
            val response = apiCall.getSingleLocationDataCoroutine(id)
            mapper.transformLocationInfoRemoteIntoLocationModel(response)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getListEpisodeModel(multiId: String): List<EpisodeModel>? {
        return try {
            val list = if (multiId.contains(',')) {
                apiCall.getMultiEpisodesData(multiId)
            } else {
                listOf(apiCall.getSingleEpisodeData(multiId.toInt()))
            }
            list?.map { mapper.transformEpisodeInfoRemoteIntoEpisodeModel(it ?: return null) }
        } catch (e: Exception) {
            null
        }
    }

    override fun getCountOfCharacters(
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): Single<Int> {
        return apiCall.getCountOfCharacters(
            nameFilter,
            statusFilter,
            speciesFilter,
            typeFilter,
            genderFilter
        )
            .map { it.pageInfo?.countOfElements }
    }

    override fun getCountOfLocations(
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?
    ): Single<Int> {
        return apiCall.getCountOfLocations(nameFilter, typeFilter, dimensionFilter)
            .map { it.pageInfo?.countOfElements }
    }

    override fun getCountOfEpisodes(nameFilter: String?, episodeFilter: String?): Single<Int> {
        return apiCall.getCountOfEpisodes(nameFilter, episodeFilter)
            .map { it.pageInfo?.countOfElements }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}