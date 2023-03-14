package com.aston.rickandmorty.di

import com.aston.rickandmorty.data.RepositoryImpl
import com.aston.rickandmorty.domain.repository.Repository
import dagger.Binds
import dagger.Module

@Module
interface DataModule {

    @Binds
    fun bindRepository(impl: RepositoryImpl): Repository
}