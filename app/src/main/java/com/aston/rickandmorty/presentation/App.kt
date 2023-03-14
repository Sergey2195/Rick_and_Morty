package com.aston.rickandmorty.presentation

import android.app.Application
import com.aston.rickandmorty.di.DaggerApplicationComponent

class App : Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}