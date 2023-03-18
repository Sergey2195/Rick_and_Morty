package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.localDataSource.dao.CharactersDao
import com.aston.rickandmorty.data.localDataSource.dao.EpisodesDao
import com.aston.rickandmorty.data.localDataSource.dao.LocationsDao
import com.aston.rickandmorty.data.localDataSource.models.CharacterInfoDto
import com.aston.rickandmorty.data.localDataSource.models.EpisodeInfoDto
import com.aston.rickandmorty.data.localDataSource.models.LocationInfoDto
import com.aston.rickandmorty.data.models.*
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.EpisodeDetailsModel
import com.aston.rickandmorty.domain.entity.EpisodeDetailsModelWithId
import com.aston.rickandmorty.mappers.Mapper
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val charactersDao: CharactersDao,
    private val locationsDao: LocationsDao,
    private val episodesDao: EpisodesDao,
    private val mapper: Mapper
) : LocalRepository {

    private var allCharactersData: List<CharacterInfoDto> = emptyList()
    private var allLocationsData: List<LocationInfoDto> = emptyList()
    private var allEpisodesData: List<EpisodeInfoDto> = emptyList()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            charactersDao.getAllFromDb().collect {
                allCharactersData = it
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            locationsDao.getAllFromDb().collect {
                allLocationsData = it
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            episodesDao.getAllFromDb().collect{
                allEpisodesData = it
            }
        }
    }

    override suspend fun getAllLocations(
        pageIndex: Int,
        nameFilter: String?,
        typeFilter: String?,
        dimensionFilter: String?
    ): AllLocationsResponse {
        var response = AllLocationsResponse(null, null)
        val filtered =
            allLocationsData.filter {
                val filteredName =
                    if (nameFilter == null) true else checkTwoStrings(nameFilter, it.locationName)
                val filteredType =
                    if (typeFilter == null) true else checkTwoStrings(typeFilter, it.locationType)
                val filteredDimension = if (dimensionFilter == null) true else checkTwoStrings(
                    dimensionFilter,
                    it.locationDimension
                )
                filteredName && filteredType && filteredDimension
            }
        val filteredItemsPage = filtered.take(pageIndex * PAGE_SIZE).drop((pageIndex - 1) * PAGE_SIZE)
            .map { mapper.transformLocationInfoDtoIntoLocationInfoRemote(it) }
        if (filteredItemsPage.isEmpty()) return response
        val countPages = filtered.size / 20 + if (filtered.size % 20 != 0) 1 else 0
        val prevPage = if (pageIndex > 1) "/page=${pageIndex - 1}" else null
        val nextPage = if (pageIndex == countPages) null else "/page=${pageIndex + 1}"
        val pageInfoResponse = PageInfoResponse(
            filtered.size,
            countPages,
            nextPage,
            prevPage
        )
        response = AllLocationsResponse(pageInfoResponse, filteredItemsPage)
        return response
    }

    override suspend fun getAllEpisodes(
        pageIndex: Int,
        nameFilter: String?,
        episodeFilter: String?
    ): AllEpisodesResponse {
        var response = AllEpisodesResponse(null, null)
        val filtered = allEpisodesData.filter {
            val filteredName =
                if (nameFilter == null) true else checkTwoStrings(nameFilter, it.episodeName)
            val filteredType =
                if (episodeFilter == null) true else checkTwoStrings(episodeFilter, it.episodeNumber)
            filteredName && filteredType
        }
        val filteredItemsPage = filtered.take(pageIndex * PAGE_SIZE).drop((pageIndex - 1) * PAGE_SIZE)
            .map { mapper.transformEpisodeInfoDtoIntoEpisodeInfoRemote(it) }
        if (filteredItemsPage.isEmpty()) return response
        val countPages = filtered.size / 20 + if (filtered.size % 20 != 0) 1 else 0
        val prevPage = if (pageIndex > 1) "/page=${pageIndex - 1}" else null
        val nextPage = if (pageIndex == countPages) null else "/page=${pageIndex + 1}"
        val pageInfoResponse = PageInfoResponse(
            filtered.size,
            countPages,
            nextPage,
            prevPage
        )
        response = AllEpisodesResponse(pageInfoResponse, filteredItemsPage)
        return response
    }

    override suspend fun getAllCharacters(
        pageIndex: Int,
        arrayFilter: Array<String?>
    ): AllCharactersResponse {
        var response = AllCharactersResponse(null, null)
        val filtered =
            allCharactersData.filter {
                val filteredName = if (arrayFilter[0] == null) true else checkTwoStrings(
                    arrayFilter[0]!!,
                    it.characterName
                )
                val filteredStatus = if (arrayFilter[1] == null) true else checkTwoStrings(
                    arrayFilter[1]!!,
                    it.characterStatus
                )
                val filteredSpecies = if (arrayFilter[2] == null) true else checkTwoStrings(
                    arrayFilter[2]!!,
                    it.characterSpecies
                )
                val filteredType = if (arrayFilter[3] == null) true else checkTwoStrings(
                    arrayFilter[3]!!,
                    it.characterType
                )
                val filteredGender = if (arrayFilter[4] == null) true else checkTwoStrings(
                    arrayFilter[4]!!,
                    it.characterGender
                )
                filteredName && filteredStatus && filteredSpecies && filteredType && filteredGender
            }
        val countPages = filtered.size / 20 + if (filtered.size % 20 != 0) 1 else 0
        val prevPage = if (pageIndex > 1) "/page=${pageIndex - 1}" else null
        val nextPage = "/page=${pageIndex + 1}"
        val pageInfoResponse = PageInfoResponse(
            filtered.size,
            countPages,
            nextPage,
            prevPage
        )
        val filteredItemsPage =
            filtered.take(pageIndex * PAGE_SIZE).drop((pageIndex - 1) * PAGE_SIZE)
                .map { mapper.transformCharacterInfoDtoIntoCharacterInfoRemote(it) }
        if (filteredItemsPage.isEmpty()) return response
        response = AllCharactersResponse(
            pageInfoResponse,
            filteredItemsPage
        )
        return response
    }

    private fun checkTwoStrings(filter: String, src: String?): Boolean {
        return src?.lowercase()?.contains(filter.lowercase()) ?: false
    }

    override suspend fun writeResponseCharacters(response: AllCharactersResponse) {
        val listCharacters = response.listCharactersInfo ?: return
        listCharacters.forEach { character ->
            charactersDao.addCharacter(
                mapper.transformCharacterInfoRemoteIntoCharacterInfoDto(
                    character
                )
            )
        }
    }

    override suspend fun writeResponseLocation(response: AllLocationsResponse?) {
        val listLocations = response?.listLocationsInfo ?: return
        listLocations.forEach { location ->
            locationsDao.addLocation(
                mapper.transformLocationInfoRemoteIntoLocationInfoDto(location)
            )
        }
    }

    override suspend fun writeResponseEpisodes(response: AllEpisodesResponse?) {
        val listEpisodes = response?.listEpisodeInfo ?: return
        listEpisodes.forEach {episode->
            episodesDao.addEpisode(
                mapper.transformEpisodeInfoRemoteIntoEpisodeInfoDto(episode)
            )
        }

    }

    override suspend fun writeSingleCharacterInfo(data: CharacterInfoRemote) {
        charactersDao.addCharacter(mapper.transformCharacterInfoRemoteIntoCharacterInfoDto(data))
    }

    override suspend fun writeSingleEpisodeInfo(data: EpisodeInfoRemote?) {
        if (data == null) return
        episodesDao.addEpisode(mapper.transformEpisodeInfoRemoteIntoEpisodeInfoDto(data))
    }

    override suspend fun deleteAllCharactersData() {
        charactersDao.deleteAllCharactersData()
    }

    override suspend fun getSingleCharacterInfo(id: Int): CharacterDetailsModel? {
        return mapper.transformCharacterInfoDtoIntoCharacterDetailsModel(
            charactersDao.getSingleCharacter(id)
        )
    }

    override suspend fun getSingleEpisodeInfo(id: Int): EpisodeDetailsModelWithId? {
        return mapper.transformEpisodeInfoDtoIntoEpisodeDetailsModelWithId(episodesDao.getEpisode(id))
    }

    override suspend fun getSingleLocationInfo(id: Int): LocationInfoDto? {
        return locationsDao.getSingleLocation(id)
    }

    override fun getSingleLocationInfoRx(id: Int): Single<LocationInfoDto> {
        return locationsDao.getSingleLocationRx(id)
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}