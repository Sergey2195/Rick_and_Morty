package com.aston.rickandmorty.data.mappers

import com.aston.rickandmorty.data.localDataSource.models.CharacterInfoDto
import com.aston.rickandmorty.data.localDataSource.models.EpisodeInfoDto
import com.aston.rickandmorty.data.localDataSource.models.LocationInfoDto
import com.aston.rickandmorty.data.remoteDataSource.models.*
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.entity.*
import com.aston.rickandmorty.utils.Utils
import javax.inject.Inject

@ApplicationScope
class Mapper @Inject constructor(
    private val utils: Utils
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
            characters = src.locationResidents?.map { utils.getLastIntAfterSlash(it) ?: 0 }
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
        return LocationDetailsModelWithId(
            locationId = src.locationId ?: 0,
            locationName = src.locationName ?: "",
            locationType = src.locationType ?: "",
            dimension = src.locationDimension ?: "",
            characters = transformStringIdIntoListInt(src.locationResidentsIds)
        )
    }

    fun transformListStringsToIds(src: List<String>?): String? {
        if (src == null) return null
        return src.map { "/${utils.getLastIntAfterSlash(it)}" }.joinToString(separator = ",")
    }

    fun transformListStringIdToStringWithoutSlash(src: List<String>?): String? {
        if (src == null) return null
        return transformListStringsToIds(src)?.replace("/", "")
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

    fun configurationEpisodeDetailsModel(
        episode: EpisodeInfoRemote?,
        characters: List<CharacterModel>
    ): EpisodeDetailsModel? {
        if (episode == null) return null
        return EpisodeDetailsModel(
            id = episode.episodeId ?: 0,
            name = episode.episodeName ?: "",
            airDate = episode.episodeAirDate ?: "",
            episodeNumber = episode.episodeNumber ?: "",
            characters = characters
        )
    }

    fun transformIdWithStringAndSlashIntoInt(str: String): Int {
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