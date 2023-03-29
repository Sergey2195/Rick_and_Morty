package com.aston.rickandmorty.utils

import com.aston.rickandmorty.di.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class Utils @Inject constructor() {
    fun findPage(str: String?): Int? {
        if (str == null) return null
        var index = str.indexOf("page=")
        if (index == -1) throw RuntimeException("findPage error")
        index += 5
        var endIndex = index
        while (endIndex < str.length && str[endIndex] >= '0' && str[endIndex] <= '9') {
            endIndex++
        }
        return try {
            str.substring(index until endIndex).toInt()
        } catch (e: Exception) {
            null
        }
    }

    fun getLastIntAfterSlash(str: String?): Int? {
        if (str == null) return null
        val lastSlash = str.lastIndexOf('/')
        if (lastSlash == -1) return null
        return str.substring(lastSlash + 1..str.lastIndex).toInt()
    }

    fun getStringForMultiId(src: List<Int?>): String {
        var resultStr = ""
        for ((index, value) in src.withIndex()) {
            if (value == null) continue
            resultStr += if (index == src.lastIndex) {
                value.toString()
            } else {
                "$value,"
            }
        }
        return resultStr
    }

    fun transformStringIdIntoListInt(src: String?): List<Int> {
        if (src == null) return emptyList()
        return try {
            src.split(",").map { (it.trim(' ', '/')).toInt() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun transformListStringsToIds(src: List<String>?): String? {
        if (src == null) return null
        return src.map { "/${getLastIntAfterSlash(it)}" }.joinToString(separator = ",")
    }

    fun transformListStringIdToStringWithoutSlash(src: List<String>?): String? {
        if (src == null) return null
        return transformListStringsToIds(src)?.replace("/", "")
    }

    fun transformStringIdToList(src: String?): List<String>? {
        if (src == null) return null
        return src.split(",").map { it.trim() }
    }

    fun transformIdWithStringAndSlashIntoInt(str: String): Int {
        return str.replace("/", "").toInt()
    }
}