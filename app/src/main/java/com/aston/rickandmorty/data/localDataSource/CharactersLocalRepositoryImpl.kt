package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.data.localDataSource.dao.CharactersDao
import com.aston.rickandmorty.data.localDataSource.models.CharacterInfoDto
import com.aston.rickandmorty.data.mappers.CharactersMapper
import com.aston.rickandmorty.data.remoteDataSource.models.AllCharactersResponse
import com.aston.rickandmorty.data.remoteDataSource.models.CharacterInfoRemote
import com.aston.rickandmorty.data.remoteDataSource.models.PageInfoResponse
import com.aston.rickandmorty.di.ApplicationScope
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@ApplicationScope
class CharactersLocalRepositoryImpl @Inject constructor(
    private val mapper: CharactersMapper,
    private val charactersDao: CharactersDao,
    private val applicationScope: CoroutineScope,
    private val utils: LocalRepositoriesUtils,
    private val pageSize: Int
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
        val filtered = filteringCharacters(filters)
        if (filtered.isEmpty()) return null
        val filteredItemsPage = takePage(filtered, pageIndex)
        if (filteredItemsPage.isEmpty()) return null
        val pageInfoResponse = pageInfo(filtered, pageIndex)
        return AllCharactersResponse(pageInfoResponse, filteredItemsPage)
    }

    private fun filteringCharacters(filters: Array<String?>): List<CharacterInfoDto> {
        return allCharactersData.filter { dto -> filteringCharacter(dto, filters) }
    }

    private fun takePage(
        filtered: List<CharacterInfoDto>,
        pageIndex: Int
    ): List<CharacterInfoRemote> {
        return filtered
            .take(pageIndex * pageSize)
            .drop((pageIndex - 1) * pageSize)
            .map { mapper.transformCharacterInfoDtoIntoCharacterInfoRemote(it) }
    }

    private fun pageInfo(filtered: List<CharacterInfoDto>, pageIndex: Int): PageInfoResponse {
        val countPages = filtered.size / pageSize + if (filtered.size % pageSize != 0) 1 else 0
        val prevPage = if (pageIndex > 1) utils.getPageString(pageIndex - 1) else null
        val nextPage = if (pageIndex == countPages) null else utils.getPageString(pageIndex + 1)
        return PageInfoResponse(filtered.size, countPages, nextPage, prevPage)
    }

    override suspend fun addCharacter(data: CharacterInfoRemote?) {
        if (data == null) return
        charactersDao.addCharacter(mapper.transformCharacterInfoRemoteIntoCharacterInfoDto(data))
    }

    override suspend fun getCharacterInfo(id: Int): CharacterInfoDto? {
        return charactersDao.getSingleCharacter(id)
    }

    override fun getCountOfCharacters(filters: Array<String?>): Single<Int> {
        val count = allCharactersData.count { dto ->
            filteringCharacter(dto, filters)
        }
        return Single.just(
            if (count == 0) -1 else count
        )
    }

    private fun filteringCharacter(dto: CharacterInfoDto, filters: Array<String?>): Boolean {
        return utils.filteringItem(filters[0], dto.characterName) && utils.filteringItem(
            filters[1],
            dto.characterStatus
        ) && utils.filteringItem(filters[2], dto.characterSpecies) && utils.filteringItem(
            filters[3],
            dto.characterType
        ) && utils.filteringItem(filters[4], dto.characterGender)
    }
}