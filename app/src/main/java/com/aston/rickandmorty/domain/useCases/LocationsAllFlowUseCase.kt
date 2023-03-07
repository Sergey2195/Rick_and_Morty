package com.aston.rickandmorty.domain.useCases

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.LocationModel
import com.aston.rickandmorty.domain.repository.Repository
import kotlinx.coroutines.flow.Flow

class LocationsAllFlowUseCase(private val rep: Repository) {
    operator fun invoke(): Flow<PagingData<LocationModel>>{
        return rep.getFlowAllLocations()
    }
}