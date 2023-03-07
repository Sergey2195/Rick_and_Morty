package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aston.rickandmorty.data.RepositoryImpl
import com.aston.rickandmorty.domain.useCases.LocationsAllFlowUseCase

class LocationsViewModel: ViewModel() {
    private val repository = RepositoryImpl
    private val locationsAllFlowUseCase = LocationsAllFlowUseCase(repository)
    val locationsAllFlow = locationsAllFlowUseCase.invoke().cachedIn(viewModelScope)
}