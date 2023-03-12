package com.aston.rickandmorty.domain.useCases

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.LocationModel
import com.aston.rickandmorty.domain.repository.Repository
import kotlinx.coroutines.flow.Flow

class LocationsAllFlowUseCase(private val rep: Repository) {
    operator fun invoke(
        nameFilter: String? = null,
        typeFilter: String? = null,
        dimensionFilter: String? = null
    ): Flow<PagingData<LocationModel>> {
        return rep.getFlowAllLocations(
            nameFilter,
            typeFilter,
            dimensionFilter
        )
    }
}