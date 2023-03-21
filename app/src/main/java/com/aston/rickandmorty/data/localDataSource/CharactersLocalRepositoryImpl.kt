package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.localDataSource.LocalRepositoriesUtils.Companion.PAGE_SIZE
import com.aston.rickandmorty.data.localDataSource.dao.CharactersDao
import com.aston.rickandmorty.data.localDataSource.models.CharacterInfoDto
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.PageInfoResponse
import com.aston.rickandmorty.mappers.Mapper
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharactersLocalRepositoryImpl @Inject constructor(
    private val mapper: Mapper,
    private val charactersDao: CharactersDao,
    private val applicationScope: CoroutineScope,
    private val utils: LocalRepositoriesUtils
) : CharactersLocalRepository {

    private var allCharactersData: List<CharacterInfoDto> = emptyList()

    init {
        collectCharactersLocalItems()
    }

    private fun collectCharactersLocalItems() = applicationScope.launch(Dispatchers.IO) {
        charactersDao.getAllFromDb().collect {
            allCharactersData = it
        }
    }

    override suspend fun getAllCharacters(
        pageIndex: Int,
        filters: Array<String?>
    ): AllCharactersResponse? {
        val filtered = allCharactersData.filter { dto ->
            filteringCharacter(dto, filters)
        }
        if (filtered.isEmpty()) return null
        val filteredItemsPage = filtered
            .take(pageIndex * PAGE_SIZE)
            .drop((pageIndex - 1) * PAGE_SIZE)
            .map { mapper.transformCharacterInfoDtoIntoCharacterInfoRemote(it) }
        if (filteredItemsPage.isEmpty()) return null
        val countPages = filtered.size / 20 + if (filtered.size % 20 != 0) 1 else 0
        val prevPage = if (pageIndex > 1) utils.getPageString(pageIndex - 1) else null
        val nextPage = if (pageIndex == countPages) null else utils.getPageString(pageIndex + 1)
        val pageInfoResponse = PageInfoResponse(filtered.size, countPages, nextPage, prevPage)
        return AllCharactersResponse(pageInfoResponse, filteredItemsPage)
    }
    override suspend fun addCharacter(data: CharacterInfoRemote?) {
        if (data == null) return
        charactersDao.addCharacter(mapper.transformCharacterInfoRemoteIntoCharacterInfoDto(data))
    }

    override suspend fun getCharacterInfo(id: Int): CharacterInfoDto? {
        return charactersDao.getSingleCharacter(id)
    }

    override fun getCountOfCharacters(filters: Array<String?>): Single<Int> {
        val count = allCharactersData.count { dto->
            filteringCharacter(dto, filters)
        }
        return Single.just(count)
    }

    private fun filteringCharacter(dto: CharacterInfoDto, filters: Array<String?>): Boolean{
        return utils.filteringItem(filters[0], dto.characterName) && utils.filteringItem(
            filters[1],
            dto.characterStatus
        ) && utils.filteringItem(filters[2], dto.characterSpecies) && utils.filteringItem(
            filters[3],
            dto.characterType
        ) && utils.filteringItem(filters[4], dto.characterGender)
    }
}