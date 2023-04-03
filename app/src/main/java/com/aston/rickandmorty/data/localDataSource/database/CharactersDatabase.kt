package com.aston.rickandmorty.data.localDataSource.database

import androidx.room.RoomDatabase
import com.aston.rickandmorty.data.localDataSource.dao.CharactersDao
import com.aston.rickandmorty.data.localDataSource.models.CharacterInfoDto

@androidx.room.Database(entities = [CharacterInfoDto::class], version = 1, exportSchema = false)
abstract class CharactersDatabase : RoomDatabase() {

    abstract fun getDao(): CharactersDao
}