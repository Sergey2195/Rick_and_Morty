package com.aston.rickandmorty.mappers

import android.content.Context
import com.aston.rickandmorty.R
import com.aston.rickandmorty.data.models.CharacterInfoRemote
import com.aston.rickandmorty.data.models.EpisodeInfoRemote
import com.aston.rickandmorty.data.models.LocationInfoRemote
import com.aston.rickandmorty.domain.entity.*
import com.aston.rickandmorty.presentation.adapterModels.*

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

    fun transformLocationInfoRemoteIntoLocationModel(src: LocationInfoRemote): LocationModel {
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

    fun transformLocationInfoRemoteIntoLocationDetailsModel(
        src: LocationInfoRemote,
        characters: List<CharacterModel>
    ): LocationDetailsModel {
        return LocationDetailsModel(
            locationId = src.locationId ?: 1,
            locationName = src.locationName ?: "",
            locationType = src.locationType ?: "",
            dimension = src.locationDimension ?: "",
            characters = characters
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

    fun transformEpisodeDetailsModelToDetailsModelAdapter(
        src: EpisodeDetailsModel,
        context: Context
    ): List<DetailsModelAdapter> {
        val list = mutableListOf<DetailsModelAdapter>(
            DetailsModelText(context.getString(R.string.character_name_title)),
            DetailsModelText(src.name),
            DetailsModelText(context.getString(R.string.air_date_title)),
            DetailsModelText(src.airDate),
            DetailsModelText(context.getString(R.string.episode_number_title)),
            DetailsModelText(src.episodeNumber)
        )
        for (character in src.characters) {
            list.add(DetailsModelCharacter(character))
        }
        return list.toList()
    }

    fun transformLocationDetailsModelToDetailsModelAdapter(
        data: LocationDetailsModel,
        context: Context
    ): List<DetailsModelAdapter> {
        val list = mutableListOf<DetailsModelAdapter>(
            DetailsModelText(context.getString(R.string.character_location_title)),
            DetailsModelText(data.locationName),
            DetailsModelText(context.getString(R.string.character_type_title)),
            DetailsModelText(data.locationType),
            DetailsModelText(context.getString(R.string.dimension_title)),
            DetailsModelText(data.dimension)
        )
        for (character in data.characters) {
            list.add(DetailsModelCharacter(character))
        }
        return list.toList()
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

    fun getListCharacterDetailsModelAdapter(
        data: CharacterDetailsModel,
        context: Context,
        originModel: LocationModel?,
        locationModel: LocationModel?,
        episodesModels: List<EpisodeModel>?
    ): List<CharacterDetailsModelAdapter> {
        val listCharacterDetails: MutableList<CharacterDetailsModelAdapter> = mutableListOf(
            CharacterDetailsImageModelAdapter(imageUrl = data.characterImage),
            CharacterDetailsTitleValueModelAdapter(
                title = context.getString(R.string.character_name_title),
                value = data.characterName
            ),
            CharacterDetailsTitleValueModelAdapter(
                title = context.getString(R.string.character_status_title),
                value = data.characterStatus
            ),
            CharacterDetailsTitleValueModelAdapter(
                title = context.getString(R.string.character_species_title),
                value = data.characterSpecies
            ),
            CharacterDetailsTitleValueModelAdapter(
                title = context.getString(R.string.character_type_title),
                value = data.characterType
            ),
            CharacterDetailsTitleValueModelAdapter(
                title = context.getString(R.string.character_gender_title),
                value = data.characterGender
            ),
        )
        if (originModel != null) {
            listCharacterDetails.add(
                CharacterDetailsTitleValueModelAdapter(
                    title = context.getString(R.string.character_origin_title),
                    value = ""
                )
            )
            listCharacterDetails.add(
                CharacterDetailsLocationModelAdapter(
                    locationModel = originModel
                )
            )
        } else {
            listCharacterDetails.add(
                CharacterDetailsTitleValueModelAdapter(
                    title = context.getString(R.string.character_origin_title),
                    value = data.characterOrigin.characterOriginName
                )
            )
        }
        if (locationModel != null) {
            listCharacterDetails.add(
                CharacterDetailsTitleValueModelAdapter(
                    title = context.getString(R.string.character_location_title),
                    value = ""
                )
            )
            listCharacterDetails.add(
                CharacterDetailsLocationModelAdapter(
                    locationModel = locationModel
                )
            )
        }
        if (episodesModels != null) {
            listCharacterDetails.add(
                CharacterDetailsTitleValueModelAdapter(
                    context.getString(R.string.episodes_title),
                    ""
                )
            )
            for (episode in episodesModels) {
                listCharacterDetails.add(
                    CharacterDetailsEpisodesModelAdapter(episode)
                )
            }
        }
        return listCharacterDetails
    }

    private const val EMPTY = "placeholder"
}