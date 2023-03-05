package com.aston.rickandmorty.mappers

import android.content.Context
import android.util.Log
import com.aston.rickandmorty.R
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.CharacterLocation
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.entity.CharacterOrigin
import com.aston.rickandmorty.presentation.adapterModels.CharacterDetailsModelAdapter

object Mapper {
    private fun transformCharacterInfoIntoCharacterModel(src: CharacterInfoRemote): CharacterModel {
        return CharacterModel(
            id = src.characterId ?: 0,
            name = src.characterName ?: EMPTY,
            species = src.characterSpecies ?: EMPTY,
            status = src.characterStatus ?: EMPTY,
            gender = src.characterGender ?: EMPTY,
            image = src.characterImage ?: EMPTY
        )
    }

    fun transformListCharacterInfoIntoListCharacterModel(src: List<CharacterInfoRemote>): List<CharacterModel> {
        return src.map { transformCharacterInfoIntoCharacterModel(it) }
    }

    fun transformCharacterInfoRemoteIntoCharacterDetailsModel(src: CharacterInfoRemote): CharacterDetailsModel {
        return CharacterDetailsModel(
            characterId = src.characterId ?: 0,
            characterName = src.characterName ?: "",
            characterStatus = src.characterStatus ?: "",
            characterSpecies = src.characterSpecies ?: "",
            characterType = src.characterType ?: "",
            characterGender = src.characterGender ?: "",
            characterOrigin = CharacterOrigin(
                characterOriginName = src.characterOriginRemote?.characterOriginName ?: "",
                characterOriginUrl = src.characterOriginRemote?.characterOriginUrl
            ),
            characterLocation = CharacterLocation(
                characterLocationName = src.characterLocationRemote?.characterLocationName ?: "",
                characterLocationUrl = src.characterLocationRemote?.characterLocationUrl
            ),
            characterImage = src.characterImage,
            characterEpisodes = src.characterEpisodes ?: emptyList(),
            characterUrl = src.characterUrl,
            characterCreated = src.characterCreated ?: ""
        )
    }

    private fun findLastNumber(str: String): String{
        val indexSlash = str.lastIndexOf('/')
        return str.substring(indexSlash+1 until str.length)
    }

    fun mapCharacterDetailsModelToListAdapterData(context: Context, src: CharacterDetailsModel): List<CharacterDetailsModelAdapter> {
        return listOf(
            CharacterDetailsModelAdapter(
                title = null,
                value = src.characterImage,
                viewType = R.layout.character_details_image
            ),
            CharacterDetailsModelAdapter(
                title = context.getString(R.string.character_name_title),
                value = src.characterName
            ),
            CharacterDetailsModelAdapter(
                title = context.getString(R.string.character_status_title),
                value = src.characterStatus
            ),
            CharacterDetailsModelAdapter(
                title = context.getString(R.string.character_species_title),
                value = src.characterSpecies
            ),
            CharacterDetailsModelAdapter(
                title = context.getString(R.string.character_type_title),
                value = src.characterType
            ),
            CharacterDetailsModelAdapter(
                title = context.getString(R.string.character_gender_title),
                value = src.characterGender
            ),
            CharacterDetailsModelAdapter(
                title = context.getString(R.string.character_origin_title),
                value = src.characterOrigin.characterOriginName,
                isClickable = true,
                url = src.characterOrigin.characterOriginUrl
            ),
            CharacterDetailsModelAdapter(
                title = context.getString(R.string.character_location_title),
                value = src.characterLocation.characterLocationName,
                isClickable = true,
                url = src.characterLocation.characterLocationUrl
            ),
            CharacterDetailsModelAdapter(
                title = context.getString(R.string.episodes_title),
                value = src.characterEpisodes.map { findLastNumber(it) }.joinToString()
            )
        )
    }

    private const val EMPTY = "placeholder"
}