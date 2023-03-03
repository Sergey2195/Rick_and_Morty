package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aston.rickandmorty.data.CharacterRepositoryImpl
import com.aston.rickandmorty.data.CharactersRepository
import com.aston.rickandmorty.domain.entity.CharacterModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CharactersViewModel: ViewModel() {
    val charactersFlow: Flow<PagingData<CharacterModel>>
    val tmpRepository = CharacterRepositoryImpl()

    init {
        charactersFlow = tmpRepository.getFlowAllCharacters().cachedIn(viewModelScope)
    }

    fun updateData(){
        //todo
    }
}