package com.aston.rickandmorty.di

import android.app.Application
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.fragments.*
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [ViewModelsModule::class, DataModule::class, NetworkModule::class, LocalModule::class])
interface ApplicationComponent {
    fun injectCharacterDetailsFragment(fragment: CharacterDetailsFragment)
    fun injectCharacterFilterFragment(fragment: CharacterFilterFragment)
    fun injectCharactersAllFragment(fragment: CharactersAllFragment)
    fun injectCharactersRootFragment(fragment: CharactersRootFragment)
    fun injectEpisodeDetailsFragment(fragment: EpisodeDetailsFragment)
    fun injectEpisodeFilterFragment(fragment: EpisodeFilterFragment)
    fun injectEpisodesAllFragment(fragment: EpisodesAllFragment)
    fun injectEpisodesRootFragment(fragment: EpisodesRootFragment)
    fun injectLocationAllFragment(fragment: LocationAllFragment)
    fun injectLocationDetailsFragment(fragment: LocationDetailsFragment)
    fun injectLocationFilterFragment(fragment: LocationFilterFragment)
    fun injectLocationsRootFragment(fragment: LocationsRootFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
        ): ApplicationComponent
    }
}