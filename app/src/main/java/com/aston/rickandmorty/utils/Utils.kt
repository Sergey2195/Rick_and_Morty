package com.aston.rickandmorty.utils

object Utils {
    fun findPage(str: String?): Int?{
        if (str == null) return null
        val lastSlash = str.lastIndexOf('/')
        val startNumber = lastSlash + 7
        var endNumber = startNumber
        while (endNumber <= str.lastIndex && str[endNumber] <= '9' && str[endNumber] >= '0'){
            endNumber++
        }
        return try {
            str.substring(startNumber, endNumber).toInt()
        }catch (e: Exception){
            null
        }
    }

    fun getLastIntAfterSlash(str: String?):Int?{
        if (str == null) return null
        val lastSlash = str.lastIndexOf('/')
        if (lastSlash == -1) return null
        return str.substring(lastSlash+1 .. str.lastIndex).toInt()
    }

    fun getStringForMultiId(src: List<Int?>): String{
        var resultStr = ""
        for ((index, value) in src.withIndex()){
            if (value == null) continue
            resultStr += if (index == src.lastIndex){
                value.toString()
            }else{
                "$value,"
            }
        }
        return resultStr
    }
}