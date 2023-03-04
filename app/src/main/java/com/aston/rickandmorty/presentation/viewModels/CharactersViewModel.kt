package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aston.rickandmorty.data.CharacterRepositoryImpl
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.useCases.CharacterAllFlowUseCase
import com.aston.rickandmorty.domain.useCases.CharacterDetailsUseCase
import kotlinx.coroutines.flow.Flow

class CharactersViewModel : ViewModel() {
    private val repository = CharacterRepositoryImpl
    private val characterAllFlowUseCase = CharacterAllFlowUseCase(repository)
    private val characterDetailsUseCase = CharacterDetailsUseCase(repository)
    val charactersFlow: Flow<PagingData<CharacterModel>> =
        characterAllFlowUseCase.invoke().cachedIn(viewModelScope)

    suspend fun getCharacterDetailsInfo(id: Int): CharacterDetailsModel?{
        return characterDetailsUseCase.invoke(id)
    }

    fun updateData() {
        //todo
    }
}