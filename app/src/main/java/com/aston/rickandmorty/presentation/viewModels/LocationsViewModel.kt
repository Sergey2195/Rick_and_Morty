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
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelAdapter
import com.aston.rickandmorty.presentation.utilsForAdapters.AdaptersUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationsViewModel @Inject constructor(
    private val locationsAllFlowUseCase: LocationsAllFlowUseCase,
    private val locationDetailsUseCase: LocationDetailsUseCase,
    private val characterDetailsUseCase: CharacterDetailsUseCase,
    private val adaptersUtils: AdaptersUtils
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
        _locationDetailsStateFlow.value = null
        val disposable = locationDetailsUseCase.invoke(id, forceUpdate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ dataWithId ->
                gotResponseFromSingle(dataWithId, forceUpdate)
            }, {})
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
        return adaptersUtils.transformLocationDetailsModelWithIdIntoLocationDetailsModel(
            data,
            sortedCharacters
        )
    }

    private suspend fun getCharacters(
        listId: List<Int>,
        forceUpdate: Boolean
    ): List<CharacterModel> {
        val listCharactersData = characterDetailsUseCase.invoke(listId, forceUpdate)
        return sortListCharacters(listCharactersData)
    }

    private fun prepareDataForAdapter(
        data: LocationDetailsModel
    ): List<DetailsModelAdapter> {
        return adaptersUtils.transformLocationDetailsModelToDetailsModelAdapter(data)
    }

    private fun sortListCharacters(list: List<CharacterModel>): List<CharacterModel> {
        return try {
            list.sortedBy { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearFilter() {
        _locationFilterStateFlow.value = null
    }

    fun setFilter(filter: LocationFilterModel) {
        _locationFilterStateFlow.value = filter
    }
}