package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.entity.LocationDetailsModel
import com.aston.rickandmorty.domain.repository.Repository
import io.reactivex.Single

class LocationDetailsUseCase(private val repository: Repository) {
    operator fun invoke(id: Int): Single<LocationDetailsModel> {
        return repository.getSingleLocationData(id)
    }
}