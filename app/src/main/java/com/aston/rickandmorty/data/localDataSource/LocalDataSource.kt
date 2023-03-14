package com.aston.rickandmorty.data.localDataSource

import android.content.Context
import com.aston.rickandmorty.data.models.AllCharactersResponse
import com.aston.rickandmorty.mappers.Mapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LocalDataSource (appContext: Context) {

    private val characterDto = CharacterDataBase.getInstance(appContext).getCharacterDao()

    fun writeToDataBase(data: AllCharactersResponse){
        val list = data.listCharactersInfo?: return
        val mappedList = list.map { Mapper.transformCharacterInfoRemoteIntoLocal(it) }
        mappedList.forEach {
            CoroutineScope(Dispatchers.IO).launch {
                characterDto.insertCharacterInfo(it)
            }
        }
    }

    fun checkContainsInDb(
        pageIndex: Int,
        nameFilter: String?,
        statusFilter: String?,
        speciesFilter: String?,
        typeFilter: String?,
        genderFilter: String?
    ): AllCharactersResponse? {
        return null
//        val list = mutableListOf<CharacterInfoRemote>()
//        for (i in 0..20) {
//            list.add(
//                CharacterInfoRemote(
//                    1,
//                    "test",
//                    "test",
//                    "test",
//                    "test",
//                    "test",
//                    CharacterOriginRemote(null, null),
//                    null,
//                    "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
//                    null,
//                    "null",
//                    null
//                )
//            )
//        }
//        if (pageIndex * 20 > list.size) return null
//        return AllCharactersResponse(
//            PageInfoResponse(20, 1, null, null), list)
    }
}