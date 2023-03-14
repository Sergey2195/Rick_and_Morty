package com.aston.rickandmorty.data.localDataSource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aston.rickandmorty.data.localDataSource.models.CharacterInfoDto

@Database(entities = [CharacterInfoDto::class], version = 1, exportSchema = false)
abstract class CharacterDataBase : RoomDatabase() {
    abstract fun getCharacterDao(): CharacterDao

    companion object {
        private var db: CharacterDataBase? = null
        private val LOCK = Any()
        fun getInstance(context: Context): CharacterDataBase {
            synchronized(LOCK){
                db?.let { return it }
                val instance =
                    Room.databaseBuilder(context, CharacterDataBase::class.java, DB_NAME).build()
                db = instance
                return instance
            }
        }
        private const val DB_NAME = "character_db"
    }
}