package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aston.rickandmorty.domain.useCases.ErrorStateFlowUseCase
import com.aston.rickandmorty.domain.useCases.GetLoadingProgressStateFlow
import com.aston.rickandmorty.domain.useCases.GetNetworkStatusIsAvailable
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.router.Router
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val loadingStateFlowUseCase: GetLoadingProgressStateFlow,
    private val getNetworkStatusIsAvailable: GetNetworkStatusIsAvailable,
    private val errorStateFlowUseCase: ErrorStateFlowUseCase
) : ViewModel() {
    private var router: Router? = null
    val isOnParentLiveData = MutableLiveData(true)

    init {
        router = Router()
    }

    fun getNetworkStatusIsAvailableStateFlow() = getNetworkStatusIsAvailable.invoke()

    fun getErrorStateFlow() = errorStateFlowUseCase.invoke()

    fun attachRouter(mainActivity: MainActivity) {
        router?.onCreate(mainActivity)
    }

    fun detachRouter() {
        router?.onDestroy()
    }

    fun openCharacterFragment() {
        router?.openCharactersFragment()
    }

    fun openLocationFragment() {
        router?.openLocationFragment()
    }

    fun openEpisodesFragment() {
        router?.openEpisodesFragment()
    }

    fun setIsOnParentLiveData(isOnParent: Boolean) {
        isOnParentLiveData.postValue(isOnParent)
    }

    fun getLoadingStateFlow() = loadingStateFlowUseCase.invoke()
}