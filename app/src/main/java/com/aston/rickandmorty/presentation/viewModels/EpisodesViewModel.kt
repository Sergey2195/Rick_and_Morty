package com.aston.rickandmorty.presentation.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aston.rickandmorty.data.RepositoryImpl
import com.aston.rickandmorty.domain.entity.EpisodeDetailsModel
import com.aston.rickandmorty.domain.entity.EpisodeFilterModel
import com.aston.rickandmorty.domain.useCases.EpisodeDetailsUseCase
import com.aston.rickandmorty.domain.useCases.EpisodesAllFlowUseCase
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EpisodesViewModel : ViewModel() {

    private val repository = RepositoryImpl
    private val episodesAllFlowUseCase = EpisodesAllFlowUseCase(repository)
    private val episodeDetailsUseCase = EpisodeDetailsUseCase(repository)
    private val _episodeFilterStateFlow = MutableStateFlow<EpisodeFilterModel?>(null)
    val episodeFilterStateFlow = _episodeFilterStateFlow.asStateFlow()

    suspend fun getEpisodeDetailsInfo(id: Int): EpisodeDetailsModel? {
        return episodeDetailsUseCase.invoke(id)
    }

    fun getEpisodesAllFlow(
        nameFilter: String? = null,
        episodeFilter: String? = null
    ) = episodesAllFlowUseCase.invoke(nameFilter, episodeFilter).cachedIn(viewModelScope)

    fun getDataToAdapter(src: EpisodeDetailsModel?, context: Context): List<DetailsModelAdapter> {
        if (src == null) return emptyList()
        return Mapper.transformEpisodeDetailsModelToDetailsModelAdapter(src, context)
    }

    fun clearFilter() {
        _episodeFilterStateFlow.value = null
    }

    fun setFilter(filter: EpisodeFilterModel) {
        _episodeFilterStateFlow.value = filter
    }
}