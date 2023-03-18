package com.aston.rickandmorty.di

import android.app.Application
import android.content.Context
import javax.inject.Inject

class ContextWrapper @Inject constructor(private val application: Application) {
    val context: Context
        get() = application.applicationContext
}