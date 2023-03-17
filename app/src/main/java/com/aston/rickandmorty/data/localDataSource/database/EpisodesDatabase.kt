package com.aston.rickandmorty.data.localDataSource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aston.rickandmorty.data.localDataSource.dao.EpisodesDao
import com.aston.rickandmorty.data.localDataSource.dao.LocationsDao
import com.aston.rickandmorty.data.localDataSource.models.EpisodeInfoDto

@Database(entities = [EpisodeInfoDto::class], version = 1, exportSchema = false)
abstract class EpisodesDatabase: RoomDatabase() {
    abstract fun getDao(): EpisodesDao
}