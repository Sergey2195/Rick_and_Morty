package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aston.rickandmorty.data.CharactersRepository
import com.aston.rickandmorty.domain.entity.CharacterModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CharactersViewModel: ViewModel() {
//    private val _charactersDataStateFlow = MutableStateFlow<List<CharacterModel>>(emptyList())
//    val charactersDataStateFlow: StateFlow<List<CharacterModel>>
//        get() = _charactersDataStateFlow.asStateFlow()
    val charactersFlow: Flow<PagingData<CharacterModel>>

    init {
        charactersFlow = CharactersRepository.getPagedCharacters().cachedIn(viewModelScope)
    }

    fun updateData(){
        getHardcodedData()
    }

    private fun getHardcodedData() {
        val list = mutableListOf<CharacterModel>()
        for (i in 0..30){
            list.add(
                CharacterModel(i, "name$i", "species$i", "status$i", "gender$i")
            )
        }
//        _charactersDataStateFlow.value = list
    }
}