package com.aston.rickandmorty.di

import com.aston.rickandmorty.data.RepositoryImpl
import com.aston.rickandmorty.data.localDataSource.LocalRepository
import com.aston.rickandmorty.data.localDataSource.LocalRepositoryImpl
import com.aston.rickandmorty.data.remoteDataSource.RemoteRepository
import com.aston.rickandmorty.data.remoteDataSource.RemoteRepositoryImpl
import com.aston.rickandmorty.domain.repository.Repository
import dagger.Binds
import dagger.Module

@Module
interface DataModule {

    @Binds
    fun bindRepository(impl: RepositoryImpl): Repository

    @Binds
    fun bindRemoteRepository(impl: RemoteRepositoryImpl): RemoteRepository

    @Binds
    fun bindLocalRepository(impl: LocalRepositoryImpl): LocalRepository
}