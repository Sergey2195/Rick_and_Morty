package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.router.Router

class MainViewModel: ViewModel() {
    private var router: Router? = null
    private var onParentFragment = true

    init {
        router = Router()
    }

    fun attachRouter(mainActivity: MainActivity) {
        router?.onCreate(mainActivity)
    }

    fun detachRouter() {
        router?.onDestroy()
    }

    fun isOnParentFragment():Boolean{
        return onParentFragment
    }

    fun setIsOnParentFragment(isOnParentFragment: Boolean){
        onParentFragment = isOnParentFragment
    }

    fun openCharacterFragment(){
        router?.openCharactersFragment()
    }

    fun openLocationFragment(){
        router?.openLocationFragment()
    }

    fun openEpisodesFragment(){
        router?.openEpisodesFragment()
    }
}