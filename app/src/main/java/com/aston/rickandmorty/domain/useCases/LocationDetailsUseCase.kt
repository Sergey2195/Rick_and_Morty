package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.entity.LocationDetailsModel
import com.aston.rickandmorty.domain.repository.Repository
import io.reactivex.Single
import javax.inject.Inject

class LocationDetailsUseCase @Inject constructor(private val repository: Repository) {
    operator fun invoke(id: Int): Single<LocationDetailsModel> {
        return repository.getSingleLocationData(id)
    }
}