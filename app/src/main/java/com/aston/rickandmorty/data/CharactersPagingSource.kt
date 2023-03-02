package com.aston.rickandmorty.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aston.rickandmorty.domain.entity.CharacterModel
import kotlinx.coroutines.delay

class CharactersPagingSource(): PagingSource<Int, CharacterModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterModel> {
        val pageIndex = params.key ?: 1
        return try {
            delay(10000)
            val resultData = CharactersRepository.hardcodedData()
            val prevPage = if (pageIndex == 1) null else pageIndex-1
            val nextPage = if (pageIndex == 10) null else pageIndex + 1
            LoadResult.Page(resultData,prevPage, nextPage)
        }catch (e: Exception){
           LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CharacterModel>): Int? {
        return null
    }
}