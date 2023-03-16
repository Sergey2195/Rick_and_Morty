package com.aston.rickandmorty.di

import android.app.Application
import androidx.room.Room
import com.aston.rickandmorty.data.localDataSource.dao.CharactersDao
import com.aston.rickandmorty.data.localDataSource.dao.LocationsDao
import com.aston.rickandmorty.data.localDataSource.database.CharactersDatabase
import com.aston.rickandmorty.data.localDataSource.database.LocationsDatabase
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

        @ApplicationScope
        @Provides
        fun provideLocationsDatabase(application: Application): LocationsDatabase{
            return Room.databaseBuilder(
                application,
                LocationsDatabase::class.java,
                "locations_db"
            ).build()
        }

        @ApplicationScope
        @Provides
        fun provideLocationsDao(locationsDatabase: LocationsDatabase): LocationsDao{
            return locationsDatabase.getDao()
        }
    }
}