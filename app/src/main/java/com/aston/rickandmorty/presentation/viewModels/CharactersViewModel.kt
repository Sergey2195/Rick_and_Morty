package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aston.rickandmorty.domain.entity.*
import com.aston.rickandmorty.domain.useCases.CharacterAllFlowUseCase
import com.aston.rickandmorty.domain.useCases.CharacterDetailsUseCase
import com.aston.rickandmorty.domain.useCases.EpisodesListWithIdsUseCase
import com.aston.rickandmorty.domain.useCases.LocationModelUseCase
import com.aston.rickandmorty.presentation.adapterModels.CharacterDetailsModelAdapter
import com.aston.rickandmorty.presentation.utilsForAdapters.AdaptersUtils
import com.aston.rickandmorty.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CharactersViewModel @Inject constructor(
    private val characterAllFlowUseCase: CharacterAllFlowUseCase,
    private val characterDetailsUseCase: CharacterDetailsUseCase,
    private val locationModelsUseCase: LocationModelUseCase,
    private val listEpisodesModelUseCase: EpisodesListWithIdsUseCase,
    private val adaptersUtils: AdaptersUtils,
    private val utils: Utils
) : ViewModel() {

    private val _dataForAdapter: MutableStateFlow<List<CharacterDetailsModelAdapter>> =
        MutableStateFlow(emptyList())
    val dataForAdapter
        get() = _dataForAdapter.asStateFlow()
    private val _characterFilter: MutableStateFlow<CharacterFilterModel?> = MutableStateFlow(null)
    val characterFilter = _characterFilter.asStateFlow()

    fun getFlowCharacters(
        nameFilter: String? = null,
        statusFilter: String? = null,
        speciesFilter: String? = null,
        typeFilter: String? = null,
        genderFilter: String? = null,
        forceUpdate: Boolean
    ): Flow<PagingData<CharacterModel>> {
        return characterAllFlowUseCase.invoke(
            nameFilter,
            statusFilter,
            speciesFilter,
            typeFilter,
            genderFilter,
            forceUpdate
        ).cachedIn(viewModelScope)
    }

    private suspend fun getOriginModel(
        data: CharacterDetailsModel,
        forceUpdate: Boolean
    ): LocationModel? {
        val originUrl = data.characterOrigin.characterOriginUrl
        val originId = utils.getLastIntAfterSlash(originUrl) ?: return null
        return locationModelsUseCase.invoke(originId, forceUpdate)
    }

    private suspend fun getLocationModel(
        data: CharacterDetailsModel,
        forceUpdate: Boolean
    ): LocationModel? {
        val locationUrl = data.characterLocation.characterLocationUrl
        val locationId = utils.getLastIntAfterSlash(locationUrl) ?: return null
        return locationModelsUseCase.invoke(locationId, forceUpdate)
    }

    private suspend fun getEpisodesModels(
        data: CharacterDetailsModel,
        forceUpdate: Boolean
    ): List<EpisodeModel>? {
        val episodes = data.characterEpisodes
        val episodesId =
            utils.getStringForMultiId(episodes.map { utils.getLastIntAfterSlash(it) })
        return listEpisodesModelUseCase.invoke(episodesId, forceUpdate)
    }

    fun loadInfoAboutCharacter(id: Int, forceUpdate: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = characterDetailsUseCase.invoke(id, forceUpdate) ?: return@launch
            val originModel = getOriginModel(data, forceUpdate)
            val locationModel = getLocationModel(data, forceUpdate)
            val episodesModels = getEpisodesModels(data, forceUpdate)
            val resultList = adaptersUtils.getListCharacterDetailsModelAdapter(
                data, originModel, locationModel, episodesModels
            )
            withContext(Dispatchers.Main) {
                _dataForAdapter.value = resultList
            }
        }
    }

    fun setCharacterFilter(filter: CharacterFilterModel) {
        _characterFilter.value = filter
    }

    fun clearCharacterFilter() {
        _characterFilter.value = null
    }

    fun clearDataForAdapter() {
        _dataForAdapter.value = emptyList()
    }

}