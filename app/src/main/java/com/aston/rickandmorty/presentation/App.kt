package com.aston.rickandmorty.presentation

import android.app.Application
import com.aston.rickandmorty.di.DaggerApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class App : Application() {
    private val appScope = CoroutineScope(Dispatchers.IO)

    val component by lazy {
        DaggerApplicationComponent.factory().create(
            this,
            appScope
        )
    }
}