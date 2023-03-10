package com.aston.rickandmorty.presentation.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aston.rickandmorty.data.RepositoryImpl
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

class CharactersViewModel : ViewModel() {

    private val repository = RepositoryImpl
    private val characterAllFlowUseCase = CharacterAllFlowUseCase(repository)
    private val characterDetailsUseCase = CharacterDetailsUseCase(repository)
    private val locationModelsUseCase = LocationModelUseCase(repository)
    private val listEpisodesModelUseCase = EpisodesListWithIdsUseCase(repository)

    val charactersFlow: Flow<PagingData<CharacterModel>> =
        characterAllFlowUseCase.invoke().cachedIn(viewModelScope)
    private val _dataForAdapter: MutableStateFlow<List<CharacterDetailsModelAdapter>> =
        MutableStateFlow(emptyList())
    val dataForAdapter
        get() = _dataForAdapter.asStateFlow()

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
}