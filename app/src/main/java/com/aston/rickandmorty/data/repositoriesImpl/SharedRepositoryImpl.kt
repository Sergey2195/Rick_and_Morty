package com.aston.rickandmorty.data.repositoriesImpl

import android.app.Application
import android.util.Log
import com.aston.rickandmorty.data.networkConnectivity.ConnectivityObserver
import com.aston.rickandmorty.data.networkConnectivity.NetworkConnectivityObserver
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.repository.SharedRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ApplicationScope
class SharedRepositoryImpl @Inject constructor(
    application: Application,
    private val applicationScope: CoroutineScope
) : SharedRepository {

    private val _loadingProgressStateFlow = MutableStateFlow(true)
    private val loadingProgressStateFlow: StateFlow<Boolean>
        get() = _loadingProgressStateFlow.asStateFlow()
    private val connectivityObserver = NetworkConnectivityObserver(application)
    private val _errorConnectionStateFlow = MutableStateFlow(false)
    override val errorStateFlow: StateFlow<Boolean>
        get() = _errorConnectionStateFlow
    private val connectionStatusIsAvailable =
        MutableStateFlow(connectivityObserver.isDeviceOnline(application.applicationContext))

    init {
        CoroutineScope(Dispatchers.IO).launch {
            connectivityObserver.observe().collect {
                connectionStatusIsAvailable.value = it == ConnectivityObserver.Status.Available
            }
        }
    }

    override fun loadingProgress(): StateFlow<Boolean> {
        return loadingProgressStateFlow
    }

    override fun setLoadingProgressStateFlow(isLoading: Boolean) {
        Log.d("SSV_REP SHARED", "is loading = $isLoading")
        _loadingProgressStateFlow.value = isLoading
    }

    override fun errorConnection(e: Exception) {
        if (e.cause?.message?.contains(NOT_FOUND_CODE) == true) return
        applicationScope.launch {
            _errorConnectionStateFlow.value = true
            delay(100)
            _errorConnectionStateFlow.value = false
        }
    }

    override fun getStateFlowIsConnected(): StateFlow<Boolean> {
        return connectionStatusIsAvailable.asStateFlow()
    }

    companion object {
        private const val NOT_FOUND_CODE = "404"
    }
}