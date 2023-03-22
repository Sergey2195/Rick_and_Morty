package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.entity.LocationModel
import com.aston.rickandmorty.domain.repository.Repository
import javax.inject.Inject

class LocationModelUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(id: Int): LocationModel?{
        return repository.getLocationModel(id)
    }
}