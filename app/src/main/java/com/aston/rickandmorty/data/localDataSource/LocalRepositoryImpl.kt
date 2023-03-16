package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.localDataSource.dao.CharactersDao
import com.aston.rickandmorty.data.localDataSource.models.CharacterInfoDto
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.PageInfoResponse
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.mappers.Mapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val charactersDao: CharactersDao,
) : LocalRepository {

    private var allCharactersData: List<CharacterInfoDto> = emptyList()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            charactersDao.getAllFromDb().collect {
                allCharactersData = it
            }
        }
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
            }.take(pageIndex * PAGE_SIZE).drop((pageIndex - 1) * 20)
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

    companion object {
        private const val PAGE_SIZE = 20
    }
}