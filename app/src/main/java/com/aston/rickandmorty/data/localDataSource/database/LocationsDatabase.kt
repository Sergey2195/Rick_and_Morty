package com.aston.rickandmorty.data.localDataSource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aston.rickandmorty.data.localDataSource.dao.LocationsDao
import com.aston.rickandmorty.data.localDataSource.models.LocationInfoDto

@Database(entities = [LocationInfoDto::class], version = 1, exportSchema = false)
abstract class LocationsDatabase : RoomDatabase() {

    abstract fun getDao(): LocationsDao
}