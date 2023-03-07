package com.aston.rickandmorty.data.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aston.rickandmorty.data.apiCalls.ApiCall
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.utils.Utils.getLastInt
import java.io.IOException

class CharactersPagingSource(private val apiCall: ApiCall) :
    PagingSource<Int, CharacterModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterModel> {
        val pageIndex = params.key ?: START_PAGE
        return try {
            val response = apiCall.getAllCharacterData(pageIndex)
            val resultData = response.listCharactersInfo ?: throw IOException()
            val prevPage = getLastInt(response.pageInfo?.prevPageUrl)
            val nextPage = getLastInt(response.pageInfo?.nextPageUrl)
            val mappedList = Mapper.transformListCharacterInfoRemoteIntoListCharacterModel(resultData)
            return LoadResult.Page(mappedList, prevPage, nextPage)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CharacterModel>): Int? {
        return null
    }

    companion object{
        private const val START_PAGE = 1
    }
}