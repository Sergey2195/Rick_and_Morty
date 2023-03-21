package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.entity.LocationDetailsModel
import com.aston.rickandmorty.domain.entity.LocationDetailsModelWithId
import com.aston.rickandmorty.domain.entity.LocationFilterModel
import com.aston.rickandmorty.domain.useCases.CharacterDetailsUseCase
import com.aston.rickandmorty.domain.useCases.LocationDetailsUseCase
import com.aston.rickandmorty.domain.useCases.LocationsAllFlowUseCase
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationsViewModel @Inject constructor(
    private val locationsAllFlowUseCase: LocationsAllFlowUseCase,
    private val locationDetailsUseCase: LocationDetailsUseCase,
    private val characterDetailsUseCase: CharacterDetailsUseCase,
    private val mapper: Mapper
) : ViewModel() {
    private val _locationFilterStateFlow: MutableStateFlow<LocationFilterModel?> =
        MutableStateFlow(null)
    val locationFilterStateFlow = _locationFilterStateFlow.asStateFlow()
    private val _locationDetailsStateFlow: MutableStateFlow<List<DetailsModelAdapter>?> =
        MutableStateFlow(null)
    val locationDetailsStateFlow
        get() = _locationDetailsStateFlow
    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getLocationAllFlow(
        nameFilter: String? = null,
        typeFilter: String? = null,
        dimensionFilter: String? = null,
        forceUpdate: Boolean = false
    ) = locationsAllFlowUseCase.invoke(nameFilter, typeFilter, dimensionFilter, forceUpdate)
        .cachedIn(viewModelScope)

    fun sendIdToGetDetails(id: Int, forceUpdate: Boolean) {
        val disposable = locationDetailsUseCase.invoke(id, forceUpdate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ dataWithId ->
                gotResponseFromSingle(dataWithId, forceUpdate)
            }, {
                locationDetailsStateFlow.value = null
            })
        compositeDisposable.add(disposable)
    }

    private fun gotResponseFromSingle(
        dataWithId: LocationDetailsModelWithId,
        forceUpdate: Boolean
    ) = viewModelScope.launch(Dispatchers.IO) {
        val locationDetailsModel = getLocationDetails(dataWithId, forceUpdate)
        val dataForAdapter = prepareDataForAdapter(locationDetailsModel)
        _locationDetailsStateFlow.value = dataForAdapter
    }

    private suspend fun getLocationDetails(
        data: LocationDetailsModelWithId,
        forceUpdate: Boolean
    ): LocationDetailsModel {
        val sortedCharacters = getCharacters(data.characters, forceUpdate)
        return mapper.transformLocationDetailsModelWithIdIntoLocationDetailsModel(
            data,
            sortedCharacters
        )
    }

    private suspend fun getCharacters(
        listId: List<Int>,
        forceUpdate: Boolean
    ): List<CharacterModel> {
        val listJob = arrayListOf<Job>()
        val listCharacters = mutableListOf<CharacterModel>()
        for (characterId in listId) {
            val job = viewModelScope.launch(Dispatchers.IO) {
                val characterData = getCharactersInfo(characterId, forceUpdate) ?: return@launch
                listCharacters.add(characterData)
            }
            listJob.add(job)
        }
        listJob.joinAll()
        return listCharacters.sortedBy { it.id }
    }

    private suspend fun getCharactersInfo(id: Int, forceUpdate: Boolean): CharacterModel? {
        val data = characterDetailsUseCase.invoke(id, forceUpdate)
        return mapper.transformCharacterDetailsModelIntoCharacterModel(data)
    }

    private fun prepareDataForAdapter(
        data: LocationDetailsModel
    ): List<DetailsModelAdapter> {
        return mapper.transformLocationDetailsModelToDetailsModelAdapter(data)
    }

    fun clearFilter() {
        _locationFilterStateFlow.value = null
    }

    fun setFilter(filter: LocationFilterModel) {
        _locationFilterStateFlow.value = filter
    }
}