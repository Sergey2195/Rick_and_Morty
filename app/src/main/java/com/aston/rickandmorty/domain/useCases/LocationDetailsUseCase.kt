package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.entity.LocationDetailsModelWithId
import com.aston.rickandmorty.domain.repository.LocationsRepository
import io.reactivex.Single
import javax.inject.Inject

class LocationDetailsUseCase @Inject constructor(private val repository: LocationsRepository) {
    operator fun invoke(id: Int, forceUpdate: Boolean): Single<LocationDetailsModelWithId> {
        return repository.getSingleLocationData(id, forceUpdate)
    }
}