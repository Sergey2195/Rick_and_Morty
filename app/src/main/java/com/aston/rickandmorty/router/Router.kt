package com.aston.rickandmorty.router

import androidx.fragment.app.Fragment
import com.aston.rickandmorty.R
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.fragments.CharactersFragment
import com.aston.rickandmorty.presentation.fragments.EpisodesFragment
import com.aston.rickandmorty.presentation.fragments.LocationsFragment

class Router {
    private var mainActivity: MainActivity? = null

    fun onCreate(activity: MainActivity) {
        mainActivity = activity
    }

    fun onDestroy() {
        mainActivity = null
    }

    fun openCharactersFragment() {
        if (mainActivity == null) return
        val fragment = mainActivity!!.supportFragmentManager.findFragmentByTag(CHARACTERS_TAG)
        val characterFragment = fragment ?: CharactersFragment.newInstance()
        openFragment(characterFragment, false, CHARACTERS_TAG, null)
    }

    fun openLocationFragment(){
        if (mainActivity == null) return
        val fragment = mainActivity!!.supportFragmentManager.findFragmentByTag(LOCATION_TAG)
        val locationFragment = fragment ?: LocationsFragment.newInstance()
        openFragment(locationFragment, true, LOCATION_TAG, LOCATION_NAME)
    }

    fun openEpisodesFragment(){
        if (mainActivity == null) return
        val fragment = mainActivity!!.supportFragmentManager.findFragmentByTag(EPISODES_TAG)
        val episodesFragment = fragment ?: EpisodesFragment.newInstance()
        openFragment(episodesFragment, true, EPISODES_TAG, EPISODES_NAME)
    }

    private fun openFragment(
        fragment: Fragment,
        addToBackStack: Boolean,
        tag: String? = null,
        backStackName: String?
    ) {
        val transaction = mainActivity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.mainFragmentContainer, fragment, tag)
        if (addToBackStack) transaction?.addToBackStack(backStackName)
        transaction?.commit()
    }

    companion object{
        const val CHARACTERS_TAG = "characters fragment"
        const val LOCATION_TAG = "location fragment"
        const val LOCATION_NAME = "location name"
        const val EPISODES_TAG = "episodes fragment"
        const val EPISODES_NAME = "episodes name"
    }

}