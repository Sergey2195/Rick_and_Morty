package com.aston.rickandmorty.presentation.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aston.rickandmorty.data.RepositoryImpl
import com.aston.rickandmorty.domain.entity.LocationDetailsModel
import com.aston.rickandmorty.domain.entity.LocationFilterModel
import com.aston.rickandmorty.domain.useCases.LocationDetailsUseCase
import com.aston.rickandmorty.domain.useCases.LocationsAllFlowUseCase
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelAdapter
import io.reactivex.Single
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationsViewModel : ViewModel() {
    private val repository = RepositoryImpl
    private val locationsAllFlowUseCase = LocationsAllFlowUseCase(repository)
    private val locationDetailsUseCase = LocationDetailsUseCase(repository)
    private val _locationFilterStateFlow: MutableStateFlow<LocationFilterModel?> =
        MutableStateFlow(null)
    val locationFilterStateFlow = _locationFilterStateFlow.asStateFlow()

    fun getLocationAllFlow(
        nameFilter: String? = null,
        typeFilter: String? = null,
        dimensionFilter: String? = null
    ) = locationsAllFlowUseCase.invoke(nameFilter, typeFilter, dimensionFilter)
        .cachedIn(viewModelScope)

    fun getLocationDetails(id: Int): Single<LocationDetailsModel> {
        return locationDetailsUseCase.invoke(id)
    }

    fun prepareDataForAdapter(
        data: LocationDetailsModel,
        context: Context
    ): List<DetailsModelAdapter> {
        return Mapper.transformLocationDetailsModelToDetailsModelAdapter(data, context)
    }

    fun clearFilter(){
        _locationFilterStateFlow.value = null
    }

    fun setFilter(filter: LocationFilterModel){
        _locationFilterStateFlow.value = filter
    }
}