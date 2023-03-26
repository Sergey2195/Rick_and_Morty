package com.aston.rickandmorty.domain.useCases

import androidx.paging.PagingData
import com.aston.rickandmorty.domain.entity.LocationModel
import com.aston.rickandmorty.domain.repository.LocationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationsAllFlowUseCase @Inject constructor(private val rep: LocationsRepository) {
    operator fun invoke(
        nameFilter: String? = null,
        typeFilter: String? = null,
        dimensionFilter: String? = null,
        forceUpdate: Boolean = false
    ): Flow<PagingData<LocationModel>> {
        return rep.getFlowAllLocations(
            arrayOf(nameFilter, typeFilter, dimensionFilter),
            forceUpdate
        )
    }
}