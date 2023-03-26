package com.aston.rickandmorty.data.mappers

import com.aston.rickandmorty.data.localDataSource.models.CharacterInfoDto
import com.aston.rickandmorty.data.remoteDataSource.models.CharacterInfoRemote
import com.aston.rickandmorty.data.remoteDataSource.models.CharacterLocationRemote
import com.aston.rickandmorty.data.remoteDataSource.models.CharacterOriginRemote
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.CharacterLocation
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.entity.CharacterOrigin
import com.aston.rickandmorty.utils.Utils
import javax.inject.Inject

@ApplicationScope
class CharactersMapper
    @Inject constructor(private val utils: Utils) {

    private fun transformCharacterInfoRemoteIntoCharacterModel(src: CharacterInfoRemote): CharacterModel {
        return CharacterModel(
            id = src.characterId ?: 0,
            name = src.characterName ?: "",
            species = src.characterSpecies ?: "",
            status = src.characterStatus ?: "",
            gender = src.characterGender ?: "",
            image = src.characterImage ?: ""
        )
    }

    fun transformListCharacterInfoRemoteIntoListCharacterModel(src: List<CharacterInfoRemote>): List<CharacterModel> {
        return src.map { transformCharacterInfoRemoteIntoCharacterModel(it) }
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
            characterEpisodes = utils.transformStringIdToList(src.characterEpisodesIds),
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
            characterEpisodes = utils.transformStringIdToList(src.characterEpisodesIds) ?: emptyList(),
            characterUrl = src.characterUrl,
            characterCreated = src.characterCreated ?: ""
        )
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
            characterEpisodesIds = utils.transformListStringsToIds(src.characterEpisodes),
            characterUrl = src.characterUrl,
            characterCreated = src.characterCreated
        )
    }
}