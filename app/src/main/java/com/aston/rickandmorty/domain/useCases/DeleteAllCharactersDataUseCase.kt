package com.aston.rickandmorty.domain.useCases

import com.aston.rickandmorty.domain.repository.Repository
import javax.inject.Inject

class DeleteAllCharactersDataUseCase @Inject constructor(private val rep: Repository) {
    suspend operator fun invoke(){
        rep.invalidateCharactersData()
    }
}