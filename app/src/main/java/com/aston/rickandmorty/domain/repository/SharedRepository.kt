package com.aston.rickandmorty.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface SharedRepository {
    fun getStateFlowIsConnected(): StateFlow<Boolean>
    fun loadingProgress(): StateFlow<Boolean>
    fun setLoadingProgressStateFlow(isLoading: Boolean)
    fun errorConnection(e: Exception)
    val errorStateFlow: StateFlow<Boolean>
}