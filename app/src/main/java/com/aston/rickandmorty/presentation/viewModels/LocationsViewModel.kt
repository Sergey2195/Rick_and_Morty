package com.aston.rickandmorty.presentation.viewModels

import android.content.Context
import android.util.Log
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
    private val characterDetailsUseCase: CharacterDetailsUseCase
) : ViewModel() {
    private val _locationFilterStateFlow: MutableStateFlow<LocationFilterModel?> =
        MutableStateFlow(null)
    val locationFilterStateFlow = _locationFilterStateFlow.asStateFlow()
    private val _locationDetailsStateFlow: MutableStateFlow<LocationDetailsModelWithId?> =
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
        dimensionFilter: String? = null
    ) = locationsAllFlowUseCase.invoke(nameFilter, typeFilter, dimensionFilter)
        .cachedIn(viewModelScope)

    fun sendIdToGetDetails(id: Int) {
        val disposable = locationDetailsUseCase.invoke(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                locationDetailsStateFlow.value = it
            }, {
                locationDetailsStateFlow.value = null
            })
        compositeDisposable.add(disposable)
    }

    fun resetLocationDetailsStateFlow(){
        locationDetailsStateFlow.value = null
    }

    suspend fun getLocationDetails(data: LocationDetailsModelWithId): LocationDetailsModel{
        val listCharacters = mutableListOf<CharacterModel>()
        val listJob = arrayListOf<Job>()
        for (characterId in data.characters) {
            val job = viewModelScope.launch(Dispatchers.IO) {
                val characterData = getCharactersInfo(characterId)
                listCharacters.add(characterData)
            }
            listJob.add(job)
        }
        if (data.characters.isNotEmpty()){
            listJob.joinAll()
        }
        return LocationDetailsModel(
            locationId = data.locationId,
            locationName = data.locationName,
            locationType = data.locationType,
            dimension = data.dimension,
            characters = listCharacters
        )
    }

    private suspend fun getCharactersInfo(id: Int): CharacterModel {
        val data = characterDetailsUseCase.invoke(id)
        return CharacterModel(
            data?.characterId ?: 0,
            data?.characterName ?: "",
            data?.characterSpecies ?: "",
            data?.characterStatus ?: "",
            data?.characterGender ?: "",
            data?.characterImage ?: ""
        )
    }

    fun prepareDataForAdapter(
        data: LocationDetailsModel,
        context: Context
    ): List<DetailsModelAdapter> {
        return Mapper.transformLocationDetailsModelToDetailsModelAdapter(data, context)
    }

    fun clearFilter() {
        _locationFilterStateFlow.value = null
    }

    fun setFilter(filter: LocationFilterModel) {
        _locationFilterStateFlow.value = filter
    }
}