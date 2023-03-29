package com.aston.rickandmorty.di

import android.app.Application
import androidx.room.Room
import com.aston.rickandmorty.data.localDataSource.dao.CharactersDao
import com.aston.rickandmorty.data.localDataSource.dao.EpisodesDao
import com.aston.rickandmorty.data.localDataSource.dao.LocationsDao
import com.aston.rickandmorty.data.localDataSource.database.CharactersDatabase
import com.aston.rickandmorty.data.localDataSource.database.EpisodesDatabase
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
                CHARACTERS_DB_NAME
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
                LOCATION_DB_NAME
            ).build()
        }

        @ApplicationScope
        @Provides
        fun provideLocationsDao(locationsDatabase: LocationsDatabase): LocationsDao{
            return locationsDatabase.getDao()
        }

        @ApplicationScope
        @Provides
        fun provideEpisodesDatabase(application: Application): EpisodesDatabase{
            return Room.databaseBuilder(
                application,
                EpisodesDatabase::class.java,
                EPISODES_DB_NAME
            ).build()
        }

        @ApplicationScope
        @Provides
        fun provideEpisodesDao(episodesDatabase: EpisodesDatabase): EpisodesDao{
            return episodesDatabase.getDao()
        }

        private const val LOCATION_DB_NAME = "locations_db"
        private const val EPISODES_DB_NAME = "episodes_db"
        private const val CHARACTERS_DB_NAME = "characters_db"
    }
}