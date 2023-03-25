package com.aston.rickandmorty.data

import android.app.Application
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.repository.SharedRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
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
        _loadingProgressStateFlow.value = isLoading
    }

    override fun errorConnection(e:Exception) {
        if (e is HttpException && e.code() == 404) return
        applicationScope.launch {
            _errorConnectionStateFlow.value = true
            delay(100)
            _errorConnectionStateFlow.value = false
        }
    }

    override fun getStateFlowIsConnected(): StateFlow<Boolean> {
        return connectionStatusIsAvailable.asStateFlow()
    }
}