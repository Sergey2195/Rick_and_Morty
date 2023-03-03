package com.aston.rickandmorty.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.CharacterModel
import kotlinx.coroutines.flow.Flow

object CharactersRepository {
    var start = 0

//    fun hardcodedData(): List<CharacterModel> {
//        val list = mutableListOf<CharacterModel>()
//        for (i in start..start + 30) {
//            list.add(
//                CharacterModel(i, "name$i", "species$i", "status$i", "gender$i")
//            )
//        }
//        start += 30
//        return list
//    }

//    fun getPagedCharacters(): Flow<PagingData<CharacterModel>> {
//        //todo pagingconfig
//        return Pager(
//            config = PagingConfig(pageSize = 30, enablePlaceholders = false, initialLoadSize = 30),
//            pagingSourceFactory = { CharactersPagingSource() }
//        ).flow
//    }
}