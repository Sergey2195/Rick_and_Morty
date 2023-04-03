package com.aston.rickandmorty.di

import androidx.paging.PagingConfig
import com.aston.rickandmorty.data.repositoriesImpl.SharedRepositoryImpl
import com.aston.rickandmorty.data.localDataSource.*
import com.aston.rickandmorty.data.remoteDataSource.*
import com.aston.rickandmorty.data.repositoriesImpl.CharactersRepositoryImpl
import com.aston.rickandmorty.data.repositoriesImpl.EpisodesRepositoryImp
import com.aston.rickandmorty.data.repositoriesImpl.LocationsRepositoryImpl
import com.aston.rickandmorty.domain.repository.CharactersRepository
import com.aston.rickandmorty.domain.repository.EpisodesRepository
import com.aston.rickandmorty.domain.repository.LocationsRepository
import com.aston.rickandmorty.domain.repository.SharedRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @Binds
    fun bindRepository(impl: SharedRepositoryImpl): SharedRepository

    @Binds
    fun bindEpisodesRepository(impl: EpisodesRepositoryImp): EpisodesRepository

    @Binds
    fun bindEpisodesRemoteRepository(impl: EpisodesRemoteRepositoryImpl): EpisodesRemoteRepository

    @Binds
    fun bindEpisodesLocalRepository(impl: EpisodesLocalRepositoryImpl): EpisodesLocalRepository

    @Binds
    fun bindCharactersRepository(impl: CharactersRepositoryImpl): CharactersRepository

    @Binds
    fun bindCharactersLocalRepository(impl: CharactersLocalRepositoryImpl): CharactersLocalRepository

    @Binds
    fun bindCharactersRemoteRepository(impl: CharactersRemoteRepositoryImpl): CharactersRemoteRepository

    @Binds
    fun bindLocationRepository(impl: LocationsRepositoryImpl): LocationsRepository

    @Binds
    fun bindLocationLocalRepository(impl: LocationLocalRepositoryImpl): LocationsLocalRepository

    @Binds
    fun bindLocationRemoteRepository(impl: LocationRemoteRepositoryImpl): LocationRemoteRepository


    companion object {
        @Provides
        fun providePageSize(): Int{
            return 20
        }

        @Provides
        fun providePagerConfig(pageSize: Int): PagingConfig {
            return PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSize = pageSize,
                prefetchDistance = 2
            )
        }
    }
}