package com.aston.rickandmorty.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface SharedRepository {
    fun getStateFlowIsConnected(): StateFlow<Boolean>
    fun getLoadingProgressStateFlow(): StateFlow<Boolean>
    fun setLoadingProgressStateFlow(isLoading: Boolean)
}