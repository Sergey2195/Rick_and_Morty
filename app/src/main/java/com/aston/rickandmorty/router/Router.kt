package com.aston.rickandmorty.router

import com.aston.rickandmorty.presentation.activities.MainActivity

class Router {
    private var mainActivity: MainActivity? = null

    fun onCreate(activity: MainActivity) {
        mainActivity = activity
    }

    fun onDestroy() {
        mainActivity = null
    }

}