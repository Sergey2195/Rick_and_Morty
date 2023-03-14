package com.aston.rickandmorty.di

import androidx.lifecycle.ViewModel
import com.aston.rickandmorty.domain.entity.CharacterFilterModel
import com.aston.rickandmorty.presentation.viewModels.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelsModule {

    @IntoMap
    @ViewModelKey(CharacterFilterViewModel::class)
    @Binds
    fun bindCharacterFilterViewModel(impl: CharacterFilterViewModel): ViewModel

    @IntoMap
    @ViewModelKey(CharactersViewModel::class)
    @Binds
    fun bindCharactersViewModel(impl: CharactersViewModel): ViewModel

    @IntoMap
    @ViewModelKey(EpisodeFilterViewModel::class)
    @Binds
    fun bindEpisodeFilterViewModel(impl: EpisodeFilterViewModel): ViewModel

    @IntoMap
    @ViewModelKey(EpisodesViewModel::class)
    @Binds
    fun bindEpisodesViewModel(impl: EpisodesViewModel): ViewModel

    @IntoMap
    @ViewModelKey(LocationFilterViewModel::class)
    @Binds
    fun bindLocationFilterViewModel(impl: LocationFilterViewModel): ViewModel

    @IntoMap
    @ViewModelKey(LocationsViewModel::class)
    @Binds
    fun bindLocationsViewModel(impl: LocationsViewModel): ViewModel

    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindMainViewModel(impl: MainViewModel): ViewModel

}