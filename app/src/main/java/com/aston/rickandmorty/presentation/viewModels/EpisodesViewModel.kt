package com.aston.rickandmorty.presentation.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aston.rickandmorty.data.RepositoryImpl
import com.aston.rickandmorty.domain.entity.EpisodeDetailsModel
import com.aston.rickandmorty.domain.useCases.EpisodeDetailsUseCase
import com.aston.rickandmorty.domain.useCases.EpisodesAllFlowUseCase
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelAdapter

class EpisodesViewModel : ViewModel() {

    private val repository = RepositoryImpl
    private val episodesAllFlowUseCase = EpisodesAllFlowUseCase(repository)
    val episodesAllFlow = episodesAllFlowUseCase.invoke().cachedIn(viewModelScope)
    private val episodeDetailsUseCase = EpisodeDetailsUseCase(repository)

    suspend fun getEpisodeDetailsInfo(id: Int): EpisodeDetailsModel? {
        return episodeDetailsUseCase.invoke(id)
    }

    fun getDataToAdapter(src: EpisodeDetailsModel?, context: Context): List<DetailsModelAdapter>{
        if (src == null) return emptyList()
        return Mapper.transformEpisodeDetailsModelToDetailsModelAdapter(src, context)
    }
}