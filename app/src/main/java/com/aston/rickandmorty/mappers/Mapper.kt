package com.aston.rickandmorty.mappers

import android.content.Context
import com.aston.rickandmorty.R
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.EpisodeInfoRemote
import com.aston.rickandmorty.data.models.LocationInfoRemote
import com.aston.rickandmorty.domain.entity.*
import com.aston.rickandmorty.presentation.adapterModels.CharacterDetailsModelAdapter
import com.aston.rickandmorty.presentation.adapterModels.EpisodeDetailsModelAdapter
import com.aston.rickandmorty.presentation.adapterModels.EpisodeDetailsModelCharacterList
import com.aston.rickandmorty.presentation.adapterModels.EpisodeDetailsModelTitleValue

object Mapper {
    private fun transformCharacterInfoRemoteIntoCharacterModel(src: CharacterInfoRemote): CharacterModel {
        return CharacterModel(
            id = src.characterId ?: 0,
            name = src.characterName ?: EMPTY,
            species = src.characterSpecies ?: EMPTY,
            status = src.characterStatus ?: EMPTY,
            gender = src.characterGender ?: EMPTY,
            image = src.characterImage ?: EMPTY
        )
    }

    private fun transformLocationInfoRemoteIntoLocationModel(src: LocationInfoRemote): LocationModel {
        return LocationModel(
            id = src.locationId ?: 0,
            name = src.locationName ?: EMPTY,
            type = src.locationType ?: EMPTY,
            dimension = src.locationDimension ?: EMPTY
        )
    }

    fun transformListCharacterInfoRemoteIntoListCharacterModel(src: List<CharacterInfoRemote>): List<CharacterModel> {
        return src.map { transformCharacterInfoRemoteIntoCharacterModel(it) }
    }

    fun transformListLocationInfoRemoteIntoListLocationModel(src: List<LocationInfoRemote>): List<LocationModel> {
        return src.map { transformLocationInfoRemoteIntoLocationModel(it) }
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

    private fun findLastNumber(str: String): String {
        val indexSlash = str.lastIndexOf('/')
        return str.substring(indexSlash + 1 until str.length)
    }

    fun mapCharacterDetailsModelToListAdapterData(
        context: Context,
        src: CharacterDetailsModel
    ): List<CharacterDetailsModelAdapter> {
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

    fun transformLocationInfoRemoteIntoLocationDetailsModel(src: LocationInfoRemote): LocationDetailsModel {
        return LocationDetailsModel(
            locationId = src.locationId ?: 0,
            locationName = src.locationName ?: "",
            locationType = src.locationType ?: "",
            dimension = src.locationDimension ?: "",
            residents = src.locationResidents ?: emptyList(),
            created = src.locationCreated ?: ""
        )
    }

    fun transformListEpisodeInfoRemoteIntoListEpisodeModel(src: List<EpisodeInfoRemote>): List<EpisodeModel> {
        return src.map { transformEpisodeInfoRemoteIntoEpisodeModel(it) }
    }

    fun transformEpisodeInfoRemoteIntoEpisodeModel(src: EpisodeInfoRemote): EpisodeModel {
        return EpisodeModel(
            id = src.episodeId ?: 0,
            name = src.episodeName ?: "",
            number = src.episodeNumber ?: "",
            dateRelease = src.episodeAirDate ?: ""
        )
    }

    fun transformEpisodeDetailsModelToEpisodeDetailsModelAdapter(
        src: EpisodeDetailsModel,
        context: Context
    ): List<EpisodeDetailsModelAdapter> {
        return listOf(
            EpisodeDetailsModelTitleValue(
                title = context.getString(R.string.character_name_title),
                value = src.name,
                viewType = R.layout.details_title_value
            ),
            EpisodeDetailsModelTitleValue(
                title = context.getString(R.string.air_date_title),
                value = src.airDate,
                viewType = R.layout.details_title_value
            ),
            EpisodeDetailsModelTitleValue(
                title = context.getString(R.string.episode_number_title),
                value = src.episodeNumber,
                viewType = R.layout.details_title_value
            ),
            EpisodeDetailsModelCharacterList(
                viewType = R.layout.details_characters_type,
                listCharacters = src.characters
            )
        )
    }

    fun transformListCharacterInfoRemoteIntoCharacterModel(src: List<CharacterInfoRemote>): List<CharacterModel> {
        return src.map { data ->
            CharacterModel(
                id = data.characterId ?: 0,
                name = data.characterName ?: "",
                species = data.characterSpecies ?: "",
                status = data.characterStatus ?: "",
                gender = data.characterGender ?: "",
                image = data.characterImage ?: ""
            )
        }
    }

    fun transformEpisodeInfoRemoteIntoEpisodeDetailsModel(
        src: EpisodeInfoRemote,
        listCharacters: List<CharacterModel>
    ): EpisodeDetailsModel {
        return EpisodeDetailsModel(
            id = src.episodeId ?: 0,
            name = src.episodeName ?: "",
            airDate = src.episodeAirDate ?: "",
            episodeNumber = src.episodeNumber ?: "",
            characters = listCharacters
        )
    }

    private const val EMPTY = "placeholder"
}