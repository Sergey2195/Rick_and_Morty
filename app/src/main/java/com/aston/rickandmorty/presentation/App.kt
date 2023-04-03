package com.aston.rickandmorty.presentation

import android.app.Application
import com.aston.rickandmorty.di.ApplicationComponent
import com.aston.rickandmorty.di.DaggerApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class App : Application() {

    private val applicationScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    override fun onCreate() {
        super.onCreate()
        if (appComponent == null) {
            appComponent = DaggerApplicationComponent
                .factory()
                .create(this, applicationScope, applicationContext)
        }
    }

    companion object {
        private var appComponent: ApplicationComponent? = null
        fun getAppComponent() =
            appComponent ?: throw RuntimeException("App component is not initialised")
    }
}