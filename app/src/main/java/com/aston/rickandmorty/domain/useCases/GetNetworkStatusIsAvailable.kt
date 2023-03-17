package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.repository.Repository
import javax.inject.Inject

class GetNetworkStatusIsAvailable @Inject constructor(private val rep: Repository) {
    operator fun invoke() = rep.getStateFlowIsConnected()
}