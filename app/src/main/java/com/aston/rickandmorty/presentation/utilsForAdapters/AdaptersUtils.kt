package com.aston.rickandmorty.presentation.utilsForAdapters

import android.content.Context
import com.aston.rickandmorty.R
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.entity.*
import com.aston.rickandmorty.presentation.adapterModels.*
import javax.inject.Inject

@ApplicationScope
class AdaptersUtils @Inject constructor(
    private val context: Context
) {

    fun transformEpisodeDetailsModelToDetailsModelAdapter(
        src: EpisodeDetailsModel
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
        data: LocationDetailsModel
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


    fun getListCharacterDetailsModelAdapter(
        data: CharacterDetailsModel,
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

    fun transformLocationDetailsModelWithIdIntoLocationDetailsModel(
        src: LocationDetailsModelWithId,
        listCharacters: List<CharacterModel>
    ): LocationDetailsModel {
        return LocationDetailsModel(
            locationId = src.locationId,
            locationName = src.locationName,
            locationType = src.locationType,
            dimension = src.dimension,
            characters = listCharacters
        )
    }
}