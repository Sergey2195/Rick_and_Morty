package com.aston.rickandmorty.router

import androidx.fragment.app.Fragment
import com.aston.rickandmorty.R
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.fragments.CharacterDetailsFragment
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
        openFragment(CharactersFragment.newInstance(), CHARACTERS_TAG, null)
    }

    fun openLocationFragment() {
        openFragment(LocationsFragment.newInstance(), LOCATION_TAG, LOCATION_NAME)
    }

    fun openEpisodesFragment() {
        openFragment(EpisodesFragment.newInstance(), EPISODES_TAG, EPISODES_NAME)
    }

    fun openCharacterDetailFragment(id: Int){
        val fragment = CharacterDetailsFragment.newInstance(id)
        openFragment(fragment, CHARACTER_DETAIL_TAG, CHARACTER_DETAIL_NAME)
    }

    private fun openFragment(fragmentIfNotFound: Fragment, tag: String?, name: String?) {
        if (mainActivity == null) return
        val findFragment = mainActivity!!.supportFragmentManager.findFragmentByTag(tag)
        val fragmentIntoTransaction = findFragment ?: fragmentIfNotFound
        val addToBackStack = tag != CHARACTERS_TAG
        conductTransaction(fragmentIntoTransaction, addToBackStack, tag, name)
    }

    private fun conductTransaction(
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

    companion object {
        const val CHARACTERS_TAG = "characters fragment"
        const val LOCATION_TAG = "location fragment"
        const val LOCATION_NAME = "location name"
        const val EPISODES_TAG = "episodes fragment"
        const val EPISODES_NAME = "episodes name"
        const val CHARACTER_DETAIL_TAG = "character detail fragment"
        const val CHARACTER_DETAIL_NAME = "character detail name"
    }
}