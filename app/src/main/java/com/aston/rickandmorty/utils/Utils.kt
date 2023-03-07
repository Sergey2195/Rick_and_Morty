package com.aston.rickandmorty.utils

object Utils {
    fun getLastIntAfterEquals(str: String?): Int?{
        if (str == null) return null
        val lastEquals = str.lastIndexOf('=')
        if (lastEquals == -1) return null
        return str.substring(lastEquals+1 .. str.lastIndex).toInt()
    }

    fun getLastIntAfterSlash(str: String?):Int?{
        if (str == null) return null
        val lastEquals = str.lastIndexOf('/')
        if (lastEquals == -1) return null
        return str.substring(lastEquals+1 .. str.lastIndex).toInt()
    }
}