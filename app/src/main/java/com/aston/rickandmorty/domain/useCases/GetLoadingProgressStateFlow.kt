package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.repository.SharedRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetLoadingProgressStateFlow @Inject constructor(private val rep: SharedRepository) {
    operator fun invoke(): StateFlow<Boolean>{
        return rep.loadingProgress()
    }
}