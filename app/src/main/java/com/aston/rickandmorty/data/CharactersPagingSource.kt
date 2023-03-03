package com.aston.rickandmorty.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aston.rickandmorty.data.apiCalls.CharacterApiCall
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.mappers.Mapper
import java.io.IOException

class CharactersPagingSource(val characterApiCall: CharacterApiCall) :
    PagingSource<Int, CharacterModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterModel> {
        val pageIndex = params.key ?: START_PAGE
        return try {
            val response = characterApiCall.getAllCharacterData(pageIndex)
            val resultData = response.listCharactersInfo ?: throw IOException()
            val prevPage = getLastInt(response.pageInfo?.prevPageUrl)
            val nextPage = getLastInt(response.pageInfo?.nextPageUrl)
            val mappedList = Mapper.transformListCharacterInfoIntoListCharacterModel(resultData)
            return LoadResult.Page(mappedList, prevPage, nextPage)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CharacterModel>): Int? {
        return null
    }

    private fun getLastInt(str: String?): Int?{
        if (str == null) return null
        val lastEquals = str.lastIndexOf('=')
        if (lastEquals == -1) return null
        return str.substring(lastEquals+1 .. str.lastIndex).toInt()
    }

    companion object{
        private const val START_PAGE = 1
    }
}