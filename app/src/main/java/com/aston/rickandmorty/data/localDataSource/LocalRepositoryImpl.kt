package com.aston.rickandmorty.data.localDataSource

import android.util.Log
import com.aston.rickandmorty.data.localDataSource.dao.CharactersDao
import com.aston.rickandmorty.data.localDataSource.dao.LocationsDao
import com.aston.rickandmorty.data.localDataSource.models.CharacterInfoDto
import com.aston.rickandmorty.data.localDataSource.models.LocationInfoDto
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.AllLocationsResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.PageInfoResponse
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.mappers.Mapper
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val charactersDao: CharactersDao,
    private val locationsDao: LocationsDao
) : LocalRepository {

    private var allCharactersData: List<CharacterInfoDto> = emptyList()
    private var allLocationsData: List<LocationInfoDto> = emptyList()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            charactersDao.getAllFromDb().collect {
                allCharactersData = it
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            locationsDao.getAllFromDb().collect{
                allLocationsData = it
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
                val filteredName = if (nameFilter == null) true else checkTwoStrings(nameFilter, it.locationName)
                val filteredType = if (typeFilter == null) true else checkTwoStrings(typeFilter, it.locationType)
                val filteredDimension = if (dimensionFilter == null) true else checkTwoStrings(dimensionFilter, it.locationDimension)
                filteredName &&filteredType && filteredDimension
            }.take(pageIndex * PAGE_SIZE).drop((pageIndex-1) * PAGE_SIZE)
                .map { Mapper.transformLocationInfoDtoIntoLocationInfoRemote(it) }
        if (filtered.isEmpty()) return response
        response = AllLocationsResponse(
            pageInfo = PageInfoResponse(null, null, "/page=${pageIndex + 1}", "/page=${pageIndex - 1}"),
            listLocationsInfo = filtered
        )
        return response
    }

    override suspend fun getAllCharacters(
        pageIndex: Int,
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): AllCharactersResponse {
        var response = AllCharactersResponse(null, null)
        val filtered =
            allCharactersData.filter {
                val filteredName =
                    if (nameFilter == null) true else checkTwoStrings(nameFilter, it.characterName)
                val filteredStatus = if (statusFilter == null) true else checkTwoStrings(
                    statusFilter,
                    it.characterStatus
                )
                val filteredSpecies = if (speciesFilter == null) true else checkTwoStrings(
                    speciesFilter,
                    it.characterSpecies
                )
                val filteredType =
                    if (typeFilter == null) true else checkTwoStrings(typeFilter, it.characterType)
                val filteredGender = if (genderFilter == null) true else checkTwoStrings(
                    genderFilter,
                    it.characterGender
                )
                filteredName && filteredStatus && filteredSpecies && filteredType && filteredGender
            }.take(pageIndex * PAGE_SIZE).drop((pageIndex - 1) * PAGE_SIZE)
                .map { Mapper.transformCharacterInfoDtoIntoCharacterInfoRemote(it) }
        if (filtered.isEmpty()) return response
        response = AllCharactersResponse(
            PageInfoResponse(null, null, "/page=${pageIndex + 1}", "/page=${pageIndex - 1}"),
            filtered
        )
        return response
    }

    private fun checkTwoStrings(filter: String, src: String?): Boolean {
        return src?.lowercase()?.contains(filter.lowercase()) ?: false
    }

    override suspend fun writeResponse(response: AllCharactersResponse) {
        val listCharacters = response.listCharactersInfo ?: return
        listCharacters.forEach { character ->
            charactersDao.addCharacter(
                Mapper.transformCharacterInfoRemoteIntoCharacterInfoDto(
                    character
                )
            )
        }
    }

    override suspend fun writeResponseLocation(response: AllLocationsResponse) {
        val listLocations = response.listLocationsInfo ?: return
        listLocations.forEach{ location->
            locationsDao.addLocation(
                Mapper.transformLocationInfoRemoteIntoLocationInfoDto(location)
            )
        }
    }

    override suspend fun writeSingleCharacterInfo(data: CharacterInfoRemote) {
        charactersDao.addCharacter(Mapper.transformCharacterInfoRemoteIntoCharacterInfoDto(data))
    }

    override suspend fun deleteAllCharactersData() {
        charactersDao.deleteAllCharactersData()
    }

    override suspend fun getSingleCharacterInfo(id: Int): CharacterDetailsModel? {
        return Mapper.transformCharacterInfoDtoIntoCharacterDetailsModel(
            charactersDao.getSingleCharacter(id)
        )
    }

    override suspend fun getSingleLocationInfo(id: Int): LocationInfoDto? {
        return locationsDao.getSingleLocation(id)
    }

    override fun getSingleLocationInfoRx(id: Int): Single<LocationInfoDto?> {
        return locationsDao.getSingleLocationRx(id)
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}