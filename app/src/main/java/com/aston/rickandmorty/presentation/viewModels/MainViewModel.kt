package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.router.Router

class MainViewModel : ViewModel() {
    private var router: Router? = null
    private var onParentFragment = true
    val searchCharacterLiveData = MutableLiveData<String>()
    val isOnParentLiveData = MutableLiveData(true)

    init {
        router = Router()
    }

    fun attachRouter(mainActivity: MainActivity) {
        router?.onCreate(mainActivity)
    }

    fun detachRouter() {
        router?.onDestroy()
    }

    fun isOnParentFragment(): Boolean {
        return onParentFragment
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

    fun setIsOnParentLiveData(isOnParent: Boolean){
        isOnParentLiveData.postValue(isOnParent)
    }

    fun addSearchCharacterLiveData(search: String) {
        searchCharacterLiveData.value = search
    }

    fun clearSearchCharacterLiveData() {
        searchCharacterLiveData.value = ""
    }

    companion object{
        const val CHARACTER_SCREEN = 1
        const val LOCATION_SCREEN = 2
        const val EPISODES_SCREEN = 3
    }
}