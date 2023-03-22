package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aston.rickandmorty.domain.entity.EpisodeFilterModel
import com.aston.rickandmorty.domain.useCases.EpisodeDetailsUseCase
import com.aston.rickandmorty.domain.useCases.EpisodesAllFlowUseCase
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class EpisodesViewModel @Inject constructor(
    private val episodesAllFlowUseCase: EpisodesAllFlowUseCase,
    private val episodeDetailsUseCase: EpisodeDetailsUseCase,
    private val mapper: Mapper
) : ViewModel() {

    private val _episodeFilterStateFlow = MutableStateFlow<EpisodeFilterModel?>(null)
    val episodeFilterStateFlow = _episodeFilterStateFlow.asStateFlow()
    private val _episodeDataForAdapter = MutableStateFlow<List<DetailsModelAdapter>?>(null)
    val episodeDataForAdapter = _episodeDataForAdapter.asStateFlow()

    fun sendIdEpisode(id: Int, forceUpdate: Boolean) = viewModelScope.launch {
        val episodeDetailsModel = episodeDetailsUseCase.invoke(id, forceUpdate) ?: return@launch
        _episodeDataForAdapter.value =
            mapper.transformEpisodeDetailsModelToDetailsModelAdapter(episodeDetailsModel)
    }

    fun getEpisodesAllFlow(
        nameFilter: String? = null,
        episodeFilter: String? = null,
        forceUpdate: Boolean
    ) = episodesAllFlowUseCase.invoke(nameFilter, episodeFilter, forceUpdate)
        .cachedIn(viewModelScope)

    fun clearFilter() {
        _episodeFilterStateFlow.value = null
    }

    fun clearEpisodeDataForAdapter(){
        _episodeDataForAdapter.value = null
    }

    fun setFilter(filter: EpisodeFilterModel) {
        _episodeFilterStateFlow.value = filter
    }
}