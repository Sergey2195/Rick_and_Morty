package com.aston.rickandmorty.data.localDataSource

import com.aston.rickandmorty.di.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class LocalRepositoriesUtils @Inject constructor() {

    fun getPageString(value: Int): String {
        return "/page=${value}"
    }

    fun filteringItem(filter: String?, src: String?): Boolean {
        if (filter == null) return true
        return src?.lowercase()?.contains(filter.lowercase()) ?: false
    }
}