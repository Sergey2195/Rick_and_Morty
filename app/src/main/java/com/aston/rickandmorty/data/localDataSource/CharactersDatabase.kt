package com.aston.rickandmorty.data.localDataSource

import androidx.room.RoomDatabase

@androidx.room.Database(entities = [CharacterInfoDto::class], version = 1, exportSchema = false)
abstract class CharactersDatabase: RoomDatabase() {
    abstract fun getDao(): CharactersDao
}