package com.aston.rickandmorty.di

import androidx.paging.PagingConfig
import com.aston.rickandmorty.data.SharedRepositoryImpl
import com.aston.rickandmorty.data.localDataSource.*
import com.aston.rickandmorty.data.remoteDataSource.*
import com.aston.rickandmorty.data.repositoriesImpl.CharactersRepositoryImpl
import com.aston.rickandmorty.data.repositoriesImpl.EpisodesRepositoryImp
import com.aston.rickandmorty.domain.repository.CharactersRepository
import com.aston.rickandmorty.domain.repository.EpisodesRepository
import com.aston.rickandmorty.domain.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @Binds
    fun bindRepository(impl: SharedRepositoryImpl): Repository

    @Binds
    fun bindRemoteRepository(impl: RemoteRepositoryImpl): RemoteRepository

    @Binds
    fun bindLocalRepository(impl: LocalRepositoryImpl): LocalRepository

    @Binds
    fun bindEpisodesRepository(impl: EpisodesRepositoryImp): EpisodesRepository

    @Binds
    fun bindEpisodesRemoteRepository(impl: EpisodesRemoteRepositoryImpl): EpisodesRemoteRepository

    @Binds
    fun bindEpisodesLocalRepository(impl: EpisodesLocalRepositoryImpl) : EpisodesLocalRepository

    @Binds
    fun bindCharactersRepository(impl: CharactersRepositoryImpl): CharactersRepository

    @Binds
    fun bindCharactersLocalRepository(impl: CharactersLocalRepositoryImpl): CharactersLocalRepository

    @Binds
    fun bindCharactersRemoteRepository(impl: CharactersRemoteRepositoryImpl): CharactersRemoteRepository


    companion object{
        @Provides
        fun providePagerConfig(): PagingConfig {
            return PagingConfig(20, enablePlaceholders = false, initialLoadSize = 20)
        }
    }
}