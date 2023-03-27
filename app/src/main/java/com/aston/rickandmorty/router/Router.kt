package com.aston.rickandmorty.router

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.aston.rickandmorty.R
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.fragments.CharactersRootFragment
import com.aston.rickandmorty.presentation.fragments.EpisodesRootFragment
import com.aston.rickandmorty.presentation.fragments.LocationsRootFragment

class Router {

    private var mainActivity: MainActivity? = null
    private var currentFragment = 0
        set(value) {
            field = value
            Log.d("SSV", "new value = $value")
        }

    fun onCreate(activity: MainActivity) {
        mainActivity = activity
    }

    fun onDestroy() {
        mainActivity = null
    }

    fun openCharactersFragment() {
        openFragment(CharactersRootFragment.newInstance(), CHARACTERS_TAG, null)
        currentFragment = CHARACTER_NUMBER
    }

    fun openLocationFragment() {
        openFragment(LocationsRootFragment.newInstance(), LOCATION_TAG, LOCATION_NAME)
        currentFragment = LOCATION_NUMBER
    }

    fun openEpisodesFragment() {
        openFragment(EpisodesRootFragment.newInstance(), EPISODES_TAG, EPISODES_NAME)
        currentFragment = EPISODE_NUMBER
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
        setAnim(transaction, fragment)
        transaction?.replace(R.id.mainFragmentContainer, fragment, tag)
        if (addToBackStack) transaction?.addToBackStack(backStackName)
        transaction?.commit()
    }

    private fun setAnim(transaction: FragmentTransaction?, fragment: Fragment) = when (fragment) {
        is CharactersRootFragment -> {
            transaction?.setCustomAnimations(R.anim.from_left, R.anim.to_right)
        }
        is EpisodesRootFragment -> transaction?.setCustomAnimations(
            R.anim.from_right,
            R.anim.to_left
        )
        else -> {
            if (currentFragment == CHARACTER_NUMBER) {
                transaction?.setCustomAnimations(R.anim.from_right, R.anim.to_left)
            } else {
                transaction?.setCustomAnimations(R.anim.from_left, R.anim.to_right)
            }
        }
    }

    companion object {
        const val CHARACTERS_TAG = "characters fragment"
        const val LOCATION_TAG = "location fragment"
        const val LOCATION_NAME = "location name"
        const val EPISODES_TAG = "episodes fragment"
        const val EPISODES_NAME = "episodes name"
        private const val CHARACTER_NUMBER = 0
        private const val LOCATION_NUMBER = 1
        private const val EPISODE_NUMBER = 2
    }
}