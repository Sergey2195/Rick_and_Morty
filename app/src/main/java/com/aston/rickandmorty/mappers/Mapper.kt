package com.aston.rickandmorty.mappers

import android.util.Log
import com.aston.rickandmorty.R
import com.aston.rickandmorty.data.localDataSource.models.CharacterInfoDto
import com.aston.rickandmorty.data.localDataSource.models.EpisodeInfoDto
import com.aston.rickandmorty.data.localDataSource.models.LocationInfoDto
import com.aston.rickandmorty.data.models.*
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.di.ContextWrapper
import com.aston.rickandmorty.domain.entity.*
import com.aston.rickandmorty.presentation.adapterModels.*
import com.aston.rickandmorty.utils.Utils
import javax.inject.Inject

@ApplicationScope
class Mapper @Inject constructor(
    private val contextWrapper: ContextWrapper
) {
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

    fun transformEpisodeInfoRemoteIntoEpisodeModel(src: EpisodeInfoRemote): EpisodeModel {
        return EpisodeModel(
            id = src.episodeId ?: 0,
            name = src.episodeName ?: "",
            number = src.episodeNumber ?: "",
            dateRelease = src.episodeAirDate ?: ""
        )
    }

    fun transformEpisodeDetailsModelToEpisodeModel(src: EpisodeDetailsModel): EpisodeModel{
        return EpisodeModel(
            id = src.id,
            name = src.name,
            number = src.episodeNumber,
            dateRelease = src.airDate
        )
    }

    fun transformEpisodeDetailsModelToDetailsModelAdapter(
        src: EpisodeDetailsModel
    ): List<DetailsModelAdapter> {
        val list = mutableListOf<DetailsModelAdapter>(
            DetailsModelText(contextWrapper.context.getString(R.string.character_name_title)),
            DetailsModelText(src.name),
            DetailsModelText(contextWrapper.context.getString(R.string.air_date_title)),
            DetailsModelText(src.airDate),
            DetailsModelText(contextWrapper.context.getString(R.string.episode_number_title)),
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
            DetailsModelText(contextWrapper.context.getString(R.string.character_location_title)),
            DetailsModelText(data.locationName),
            DetailsModelText(contextWrapper.context.getString(R.string.character_type_title)),
            DetailsModelText(data.locationType),
            DetailsModelText(contextWrapper.context.getString(R.string.dimension_title)),
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

    fun transformLocationInfoRemoteInfoLocationDetailsModelWithIds(src: LocationInfoRemote): LocationDetailsModelWithId {
        return LocationDetailsModelWithId(
            locationId = src.locationId ?: 0,
            locationName = src.locationName ?: "",
            locationType = src.locationType ?: "",
            dimension = src.locationDimension ?: "",
            characters = src.locationResidents?.map { Utils.getLastIntAfterSlash(it) ?: 0 }
                ?: emptyList()
        )
    }

    fun transformStringIdIntoListInt(src: String?): List<Int> {
        if (src == null) return emptyList()
        return try {
            src.split(",").map { (it.trim(' ', '/')).toInt() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun transformCharacterDetailsModelIntoCharacterModel(src: CharacterDetailsModel?): CharacterModel? {
        if (src == null) return null
        return CharacterModel(
            src.characterId,
            src.characterName,
            src.characterSpecies,
            src.characterStatus,
            src.characterGender,
            src.characterImage ?: ""
        )
    }

    fun transformLocationDtoIntoLocationDetailsWithIds(src: LocationInfoDto): LocationDetailsModelWithId {
        Log.d("SSV", src.toString())
        return LocationDetailsModelWithId(
            locationId = src.locationId ?: 0,
            locationName = src.locationName ?: "",
            locationType = src.locationType ?: "",
            dimension = src.locationDimension ?: "",
            characters = transformStringIdIntoListInt(src.locationResidentsIds)
        )
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
        originModel: LocationModel?,
        locationModel: LocationModel?,
        episodesModels: List<EpisodeModel>?
    ): List<CharacterDetailsModelAdapter> {
        val listCharacterDetails: MutableList<CharacterDetailsModelAdapter> = mutableListOf(
            CharacterDetailsImageModelAdapter(imageUrl = data.characterImage),
            CharacterDetailsTitleValueModelAdapter(
                title = contextWrapper.context.getString(R.string.character_name_title),
                value = data.characterName
            ),
            CharacterDetailsTitleValueModelAdapter(
                title = contextWrapper.context.getString(R.string.character_status_title),
                value = data.characterStatus
            ),
            CharacterDetailsTitleValueModelAdapter(
                title = contextWrapper.context.getString(R.string.character_species_title),
                value = data.characterSpecies
            ),
            CharacterDetailsTitleValueModelAdapter(
                title = contextWrapper.context.getString(R.string.character_type_title),
                value = data.characterType
            ),
            CharacterDetailsTitleValueModelAdapter(
                title = contextWrapper.context.getString(R.string.character_gender_title),
                value = data.characterGender
            ),
        )
        if (originModel != null) {
            listCharacterDetails.add(
                CharacterDetailsTitleValueModelAdapter(
                    title = contextWrapper.context.getString(R.string.character_origin_title),
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
                    title = contextWrapper.context.getString(R.string.character_origin_title),
                    value = data.characterOrigin.characterOriginName
                )
            )
        }
        if (locationModel != null) {
            listCharacterDetails.add(
                CharacterDetailsTitleValueModelAdapter(
                    title = contextWrapper.context.getString(R.string.character_location_title),
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
                    contextWrapper.context.getString(R.string.episodes_title),
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

    fun transformListStringsToIds(src: List<String>?): String? {
        if (src == null) return null
        return src.map { "/${Utils.getLastIntAfterSlash(it)}"}.joinToString(separator = ",")
    }

    fun transformListStringIdToStringWithoutSlash(src: List<String>?): String?{
        if (src == null) return null
        return transformListStringsToIds(src)?.replace("/","")
    }

    private fun transformStringIdToList(src: String?): List<String>? {
        if (src == null) return null
        return src.split(",").map { it.trim() }
    }

    fun transformLocationInfoDtoIntoLocationInfoRemote(src: LocationInfoDto): LocationInfoRemote {
        return LocationInfoRemote(
            locationId = src.locationId,
            locationName = src.locationName,
            locationType = src.locationType,
            locationDimension = src.locationDimension,
            locationResidents = transformStringIdToList(src.locationResidentsIds),
            locationUrl = src.locationUrl,
            locationCreated = src.locationCreated
        )
    }

    fun transformLocationInfoRemoteIntoLocationInfoDto(src: LocationInfoRemote): LocationInfoDto {
        return LocationInfoDto(
            locationId = src.locationId,
            locationName = src.locationName,
            locationType = src.locationType,
            locationDimension = src.locationDimension,
            locationResidentsIds = transformListStringsToIds(src.locationResidents),
            locationUrl = src.locationUrl,
            locationCreated = src.locationCreated
        )
    }

    fun transformEpisodeInfoRemoteIntoEpisodeInfoDto(src: EpisodeInfoRemote): EpisodeInfoDto {
        return EpisodeInfoDto(
            episodeId = src.episodeId,
            episodeName = src.episodeName,
            episodeAirDate = src.episodeAirDate,
            episodeNumber = src.episodeNumber,
            episodeCharacters = transformListStringsToIds(src.episodeCharacters),
            episodeUrl = src.episodeUrl,
            episodeCreated = src.episodeCreated
        )
    }

    fun transformCharacterInfoDtoIntoCharacterInfoRemote(src: CharacterInfoDto): CharacterInfoRemote {
        return CharacterInfoRemote(
            characterId = src.characterId,
            characterName = src.characterName,
            characterStatus = src.characterStatus,
            characterSpecies = src.characterSpecies,
            characterType = src.characterType,
            characterGender = src.characterGender,
            characterOriginRemote = CharacterOriginRemote(
                characterOriginName = src.characterOriginName,
                characterOriginUrl = src.characterOriginUrl
            ),
            characterLocationRemote = CharacterLocationRemote(
                characterLocationName = src.characterLocationName,
                characterLocationUrl = src.characterLocationUrl
            ),
            characterImage = src.characterImage,
            characterEpisodes = transformStringIdToList(src.characterEpisodesIds),
            characterUrl = src.characterUrl,
            characterCreated = src.characterCreated
        )
    }

    fun transformCharacterInfoDtoIntoCharacterDetailsModel(src: CharacterInfoDto?): CharacterDetailsModel? {
        if (src == null) return null
        return CharacterDetailsModel(
            characterId = src.characterId ?: 0,
            characterName = src.characterName ?: "",
            characterStatus = src.characterStatus ?: "",
            characterSpecies = src.characterSpecies ?: "",
            characterType = src.characterType ?: "",
            characterGender = src.characterGender ?: "",
            characterOrigin = CharacterOrigin(
                characterOriginName = src.characterOriginName ?: "",
                characterOriginUrl = src.characterOriginUrl
            ),
            characterLocation = CharacterLocation(
                characterLocationName = src.characterLocationName ?: "",
                characterLocationUrl = src.characterLocationUrl
            ),
            characterImage = src.characterImage,
            characterEpisodes = transformStringIdToList(src.characterEpisodesIds) ?: emptyList(),
            characterUrl = src.characterUrl,
            characterCreated = src.characterCreated ?: ""
        )
    }

    fun transformEpisodeInfoDtoIntoEpisodeDetailsModelWithId(src: EpisodeInfoDto?): EpisodeDetailsModelWithId? {
        if (src == null) return null
        return EpisodeDetailsModelWithId(
            id = src.episodeId ?: return null,
            name = src.episodeName ?: return null,
            airDate = src.episodeAirDate ?: return null,
            episodeNumber = src.episodeNumber ?: return null,
            characters = transformStringIdIntoListInt(src.episodeCharacters)
        )
    }

    fun configurationEpisodeDetailsModel(episode: EpisodeInfoRemote?, characters: List<CharacterModel>): EpisodeDetailsModel?{
        if (episode == null) return  null
        return EpisodeDetailsModel(
            id = episode.episodeId ?: 0,
            name = episode.episodeName ?: "",
            airDate = episode.episodeAirDate ?: "",
            episodeNumber = episode.episodeNumber ?: "",
            characters = characters
        )
    }

    fun transformIdWithStringAndSlashIntoInt(str: String): Int{
        return str.replace("/", "").toInt()
    }

    fun transformCharacterInfoRemoteIntoCharacterInfoDto(src: CharacterInfoRemote): CharacterInfoDto {
        return CharacterInfoDto(
            characterId = src.characterId,
            characterName = src.characterName,
            characterStatus = src.characterStatus,
            characterSpecies = src.characterSpecies,
            characterType = src.characterType,
            characterGender = src.characterGender,
            characterOriginName = src.characterOriginRemote?.characterOriginName,
            characterOriginUrl = src.characterOriginRemote?.characterOriginUrl,
            characterLocationName = src.characterLocationRemote?.characterLocationName,
            characterLocationUrl = src.characterLocationRemote?.characterLocationUrl,
            characterImage = src.characterImage,
            characterEpisodesIds = transformListStringsToIds(src.characterEpisodes),
            characterUrl = src.characterUrl,
            characterCreated = src.characterCreated
        )
    }

    fun transformEpisodeInfoDtoIntoEpisodeInfoRemote(src: EpisodeInfoDto): EpisodeInfoRemote {
        return EpisodeInfoRemote(
            episodeId = src.episodeId,
            episodeName = src.episodeName,
            episodeAirDate = src.episodeAirDate,
            episodeNumber = src.episodeNumber,
            episodeCharacters = transformStringIdToList(src.episodeCharacters),
            episodeUrl = src.episodeUrl,
            episodeCreated = src.episodeCreated
        )
    }

    companion object {
        private const val EMPTY = "placeholder"
    }
}