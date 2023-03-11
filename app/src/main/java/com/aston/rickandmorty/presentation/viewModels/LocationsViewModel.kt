package com.aston.rickandmorty.presentation.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aston.rickandmorty.R
import com.aston.rickandmorty.data.RepositoryImpl
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.LocationDetailsModel
import com.aston.rickandmorty.domain.useCases.CharacterDetailsUseCase
import com.aston.rickandmorty.domain.useCases.LocationDetailsUseCase
import com.aston.rickandmorty.domain.useCases.LocationsAllFlowUseCase
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelAdapter
import io.reactivex.Single

class LocationsViewModel: ViewModel() {
    private val repository = RepositoryImpl
    private val locationsAllFlowUseCase = LocationsAllFlowUseCase(repository)
    private val locationDetailsUseCase = LocationDetailsUseCase(repository)
    private val characterDetailsUseCase = CharacterDetailsUseCase(repository)
    val locationsAllFlow = locationsAllFlowUseCase.invoke().cachedIn(viewModelScope)

    fun getLocationDetails(id:Int): Single<LocationDetailsModel> {
        return locationDetailsUseCase.invoke(id)
    }

    fun prepareDataForAdapter(data: LocationDetailsModel, context: Context): List<DetailsModelAdapter>{
        return Mapper.transformLocationDetailsModelToDetailsModelAdapter(data, context)
    }

    suspend fun getCharacterDetails(id: Int): CharacterDetailsModel?{
        return characterDetailsUseCase.invoke(id)
    }


}