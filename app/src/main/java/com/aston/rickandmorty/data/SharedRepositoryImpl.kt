package com.aston.rickandmorty.data

import android.app.Application
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.repository.SharedRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ApplicationScope
class SharedRepositoryImpl @Inject constructor(
    application: Application,
) : SharedRepository {

    private val loadingProgressStateFlow = MutableStateFlow(true)
    private val connectivityObserver = NetworkConnectivityObserver(application)
    private val connectionStatusIsAvailable =
        MutableStateFlow(connectivityObserver.isDeviceOnline(application.applicationContext))

    init {
        CoroutineScope(Dispatchers.IO).launch {
            connectivityObserver.observe().collect {
                connectionStatusIsAvailable.value = it == ConnectivityObserver.Status.Available
            }
        }
    }

    override fun getLoadingProgressStateFlow(): StateFlow<Boolean> {
        return loadingProgressStateFlow.asStateFlow()
    }

    override fun setLoadingProgressStateFlow(isLoading: Boolean) {
        loadingProgressStateFlow.value = isLoading
    }


    override fun getStateFlowIsConnected(): StateFlow<Boolean> {
        return connectionStatusIsAvailable.asStateFlow()
    }
}