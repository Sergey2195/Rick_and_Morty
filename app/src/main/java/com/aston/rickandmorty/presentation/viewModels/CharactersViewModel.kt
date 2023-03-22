package com.aston.rickandmorty.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aston.rickandmorty.domain.entity.CharacterFilterModel
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.entity.LocationModel
import com.aston.rickandmorty.domain.useCases.CharacterAllFlowUseCase
import com.aston.rickandmorty.domain.useCases.CharacterDetailsUseCase
import com.aston.rickandmorty.domain.useCases.EpisodesListWithIdsUseCase
import com.aston.rickandmorty.domain.useCases.LocationModelUseCase
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.presentation.adapterModels.CharacterDetailsModelAdapter
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
    private val mapper: Mapper
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

    fun loadInfoAboutCharacter(id: Int, forceUpdate: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            //todo need refactoring
            Log.d("SSV_1", "${System.currentTimeMillis()}")
            val data = characterDetailsUseCase.invoke(id, forceUpdate) ?: return@launch
            Log.d("SSV_2", "${System.currentTimeMillis()}")
            val originUrl = data.characterOrigin.characterOriginUrl
            val originId = Utils.getLastIntAfterSlash(originUrl)
            var originModel: LocationModel? = null
            var locationModel: LocationModel? = null
            if (originId != null) {
                originModel = locationModelsUseCase.invoke(originId, forceUpdate)
            }
            Log.d("SSV_3", "${System.currentTimeMillis()}")
            val locationUrl = data.characterLocation.characterLocationUrl
            val locationId = Utils.getLastIntAfterSlash(locationUrl)
            if (locationId != null) {
                locationModel = locationModelsUseCase.invoke(locationId, forceUpdate)
            }
            Log.d("SSV_4", "${System.currentTimeMillis()}")
            val episodes = data.characterEpisodes
            val episodesId =
                Utils.getStringForMultiId(episodes.map { Utils.getLastIntAfterSlash(it) })
            Log.d("SSV_4.5", "${System.currentTimeMillis()}")
            Log.d("SSV_4.5", "${episodesId}")
            val episodesModels = listEpisodesModelUseCase.invoke(episodesId, forceUpdate)
            Log.d("SSV_5", "${System.currentTimeMillis()}")
            val resultList = mapper.getListCharacterDetailsModelAdapter(
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

    fun clearDataForAdapter(){
        _dataForAdapter.value = emptyList()
    }

}