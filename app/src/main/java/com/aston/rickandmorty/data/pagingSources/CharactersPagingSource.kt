package com.aston.rickandmorty.data.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.utils.Utils
import retrofit2.HttpException
import java.io.IOException

class CharactersPagingSource(private val loader: suspend (pageIndex: Int) -> AllCharactersResponse) :
    PagingSource<Int, CharacterModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterModel> {
        val pageIndex = params.key ?: START_PAGE
        return try {
            val response = loader.invoke(pageIndex)
            val resultData = response.listCharactersInfo ?: throw IOException()
            val prevPage = Utils.findPage(response.pageInfo?.prevPageUrl)
            val nextPage = Utils.findPage(response.pageInfo?.nextPageUrl)
            val mappedList =
                Mapper.transformListCharacterInfoRemoteIntoListCharacterModel(resultData)
            return LoadResult.Page(mappedList, prevPage, nextPage)
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 404) {
                return LoadResult.Page(emptyList(), pageIndex - 1, null)
            }
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CharacterModel>): Int? {
        return null
    }

    companion object {
        private const val START_PAGE = 1
    }
}