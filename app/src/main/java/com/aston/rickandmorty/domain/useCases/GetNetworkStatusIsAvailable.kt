package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.repository.SharedRepository
import javax.inject.Inject

class GetNetworkStatusIsAvailable @Inject constructor(private val rep: SharedRepository) {
    operator fun invoke() = rep.getStateFlowIsConnected()
}