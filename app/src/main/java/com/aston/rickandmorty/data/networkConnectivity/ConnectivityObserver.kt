package com.aston.rickandmorty.data.networkConnectivity

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    enum class Status {
        Available, Unavailable, Lost, Losing
    }

    fun observe(): Flow<Status>
    fun isDeviceOnline(context: Context): Boolean
}