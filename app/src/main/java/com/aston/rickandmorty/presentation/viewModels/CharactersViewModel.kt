package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aston.rickandmorty.data.CharacterRepositoryImpl
import com.aston.rickandmorty.domain.entity.CharacterModel
import kotlinx.coroutines.flow.Flow

class CharactersViewModel : ViewModel() {
    val charactersFlow: Flow<PagingData<CharacterModel>>
    val tmpRepository = CharacterRepositoryImpl()

    init {
        charactersFlow = tmpRepository.getFlowAllCharacters().cachedIn(viewModelScope)
    }

    fun updateData() {
        //todo
    }
}