package com.aston.rickandmorty.data.localDataSource

import android.util.Log
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.PageInfoResponse
import com.aston.rickandmorty.mappers.Mapper
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val charactersDao: CharactersDao,
) : LocalRepository {
    override suspend fun getAllCharacters(
        pageIndex: Int,
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): AllCharactersResponse {
        var response = AllCharactersResponse(null, null)
        val allDataCharacters = charactersDao.getAllFromDb()
        val filtered =
            allDataCharacters.filter {
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

    fun checkTwoStrings(filter: String, src: String?): Boolean {
        return src?.lowercase()?.contains(filter.lowercase()) ?: false
    }

    override suspend fun writeResponse(response: AllCharactersResponse) {
        val listCharacters = response.listCharactersInfo ?: return
        listCharacters.forEach { character ->
            Log.d("SSV_REP", "writeResponse $character")
            charactersDao.addCharacter(
                Mapper.transformCharacterInfoRemoteIntoCharacterInfoDto(
                    character
                )
            )
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}