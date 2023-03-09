package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aston.rickandmorty.data.RepositoryImpl
import com.aston.rickandmorty.domain.useCases.EpisodesAllFlowUseCase

class EpisodesViewModel: ViewModel() {

    private val repository = RepositoryImpl
    private val episodesAllFlowUseCase = EpisodesAllFlowUseCase(repository)
    val episodesAllFlow = episodesAllFlowUseCase.invoke().cachedIn(viewModelScope)
}