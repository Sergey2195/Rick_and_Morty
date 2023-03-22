package com.aston.rickandmorty.presentation.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aston.rickandmorty.data.RepositoryImpl
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
    private val listEpisodesModelUseCase: EpisodesListWithIdsUseCase
): ViewModel() {

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
        genderFilter: String? = null
    ): Flow<PagingData<CharacterModel>>{
        return characterAllFlowUseCase.invoke(nameFilter, statusFilter, speciesFilter, typeFilter, genderFilter)
            .cachedIn(viewModelScope)
    }

    fun loadInfoAboutCharacter(id: Int, context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val data = characterDetailsUseCase.invoke(id) ?: return@launch
        val originUrl = data.characterOrigin.characterOriginUrl
        val originId = Utils.getLastIntAfterSlash(originUrl)
        var originModel: LocationModel? = null
        var locationModel: LocationModel? = null
        if (originId != null) {
            originModel = locationModelsUseCase.invoke(originId)
        }
        val locationUrl = data.characterLocation.characterLocationUrl
        val locationId = Utils.getLastIntAfterSlash(locationUrl)
        if (locationId != null) {
            locationModel = locationModelsUseCase.invoke(locationId)
        }
        val episodes = data.characterEpisodes
        val episodesId = Utils.getStringForMultiId(episodes.map { Utils.getLastIntAfterSlash(it) })
        val episodesModels = listEpisodesModelUseCase.invoke(episodesId)
        val resultList = Mapper.getListCharacterDetailsModelAdapter(
            data, context, originModel, locationModel, episodesModels
        )
        withContext(Dispatchers.Main) {
            _dataForAdapter.value = resultList
        }
    }

    fun setCharacterFilter(filter: CharacterFilterModel){
        _characterFilter.value = filter
    }

    fun clearCharacterFilter(){
        _characterFilter.value = null
    }
}