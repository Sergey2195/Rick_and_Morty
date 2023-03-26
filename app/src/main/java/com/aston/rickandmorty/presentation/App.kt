package com.aston.rickandmorty.presentation

import android.app.Application
import com.aston.rickandmorty.di.DaggerApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class App : Application() {

    private val appScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }
    private val component by lazy {
        DaggerApplicationComponent.factory().create(
            app,
            appScope,
            app.applicationContext
        )
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object{
        private lateinit var app: App
        fun getAppComponent() = app.component
    }
}