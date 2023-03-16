package com.aston.rickandmorty.di

import android.app.Application
import androidx.room.Room
import com.aston.rickandmorty.data.localDataSource.dao.CharactersDao
import com.aston.rickandmorty.data.localDataSource.database.CharactersDatabase
import dagger.Module
import dagger.Provides

@Module
interface LocalModule {

    companion object {
        @ApplicationScope
        @Provides
        fun provideCharactersDatabase(application: Application): CharactersDatabase {
            return Room.databaseBuilder(
                application,
                CharactersDatabase::class.java,
                "characters_db"
            ).build()
        }

        @ApplicationScope
        @Provides
        fun provideCharacterDao(charactersDatabase: CharactersDatabase): CharactersDao {
            return charactersDatabase.getDao()
        }
    }
}