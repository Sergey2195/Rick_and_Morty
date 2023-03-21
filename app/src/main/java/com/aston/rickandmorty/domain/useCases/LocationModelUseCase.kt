package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.entity.LocationModel
import com.aston.rickandmorty.domain.repository.LocationsRepository
import javax.inject.Inject

class LocationModelUseCase @Inject constructor(private val repository: LocationsRepository) {
    suspend operator fun invoke(id: Int, forceUpdate: Boolean = false): LocationModel?{
        return repository.getLocationModel(id, forceUpdate)
    }
}