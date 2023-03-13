package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aston.rickandmorty.data.RepositoryImpl
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.LocationDetailsModel
import com.aston.rickandmorty.domain.useCases.CharacterDetailsUseCase
import com.aston.rickandmorty.domain.useCases.LocationDetailsUseCase
import com.aston.rickandmorty.domain.useCases.LocationsAllFlowUseCase
import com.aston.rickandmorty.utils.Utils
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

    fun getIdsFromUrl(list: List<String>): List<Int>{
        return list.map { Utils.getLastIntAfterSlash(it) ?: -1 }
    }

    suspend fun getCharacterDetails(id: Int): CharacterDetailsModel?{
        return characterDetailsUseCase.invoke(id)
    }


}