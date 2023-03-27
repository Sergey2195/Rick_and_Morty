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
        return with(src) {
            CharacterModel(
                id = characterId ?: 0,
                name = characterName ?: "",
                species = characterSpecies ?: "",
                status = characterStatus ?: "",
                gender = characterGender ?: "",
                image = characterImage ?: ""
            )
        }
    }

    fun transformListCharacterInfoRemoteIntoListCharacterModel(src: List<CharacterInfoRemote>): List<CharacterModel> {
        return src.map { transformCharacterInfoRemoteIntoCharacterModel(it) }
    }

    fun transformCharacterInfoRemoteIntoCharacterDetailsModel(src: CharacterInfoRemote): CharacterDetailsModel {
        return with(src) {
            CharacterDetailsModel(
                characterId = characterId ?: 0,
                characterName = characterName ?: "",
                characterStatus = characterStatus ?: "",
                characterSpecies = characterSpecies ?: "",
                characterType = characterType ?: "",
                characterGender = characterGender ?: "",
                characterOrigin = CharacterOrigin(
                    characterOriginName = characterOriginRemote?.characterOriginName ?: "",
                    characterOriginUrl = characterOriginRemote?.characterOriginUrl
                ),
                characterLocation = CharacterLocation(
                    characterLocationName = characterLocationRemote?.characterLocationName ?: "",
                    characterLocationUrl = characterLocationRemote?.characterLocationUrl
                ),
                characterImage = characterImage,
                characterEpisodes = characterEpisodes ?: emptyList(),
                characterUrl = characterUrl,
                characterCreated = characterCreated ?: ""
            )
        }
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
        with(src) {
            return CharacterInfoRemote(
                characterId = characterId,
                characterName = characterName,
                characterStatus = characterStatus,
                characterSpecies = characterSpecies,
                characterType = characterType,
                characterGender = characterGender,
                characterOriginRemote = CharacterOriginRemote(
                    characterOriginName = characterOriginName,
                    characterOriginUrl = characterOriginUrl
                ),
                characterLocationRemote = CharacterLocationRemote(
                    characterLocationName = characterLocationName,
                    characterLocationUrl = characterLocationUrl
                ),
                characterImage = characterImage,
                characterEpisodes = utils.transformStringIdToList(characterEpisodesIds),
                characterUrl = characterUrl,
                characterCreated = characterCreated
            )
        }
    }


    fun transformCharacterInfoDtoIntoCharacterDetailsModel(src: CharacterInfoDto?): CharacterDetailsModel? {
        if (src == null) return null
        with(src) {
            return CharacterDetailsModel(
                characterId = characterId ?: 0,
                characterName = characterName ?: "",
                characterStatus = characterStatus ?: "",
                characterSpecies = characterSpecies ?: "",
                characterType = characterType ?: "",
                characterGender = characterGender ?: "",
                characterOrigin = CharacterOrigin(
                    characterOriginName = characterOriginName ?: "",
                    characterOriginUrl = characterOriginUrl
                ),
                characterLocation = CharacterLocation(
                    characterLocationName = characterLocationName ?: "",
                    characterLocationUrl = characterLocationUrl
                ),
                characterImage = characterImage,
                characterEpisodes = utils.transformStringIdToList(characterEpisodesIds)
                    ?: emptyList(),
                characterUrl = characterUrl,
                characterCreated = characterCreated ?: ""
            )
        }
    }

    fun transformCharacterInfoRemoteIntoCharacterInfoDto(src: CharacterInfoRemote): CharacterInfoDto {
        with(src) {
            return CharacterInfoDto(
                characterId = characterId,
                characterName = characterName,
                characterStatus = characterStatus,
                characterSpecies = characterSpecies,
                characterType = characterType,
                characterGender = characterGender,
                characterOriginName = characterOriginRemote?.characterOriginName,
                characterOriginUrl = characterOriginRemote?.characterOriginUrl,
                characterLocationName = characterLocationRemote?.characterLocationName,
                characterLocationUrl = characterLocationRemote?.characterLocationUrl,
                characterImage = characterImage,
                characterEpisodesIds = utils.transformListStringsToIds(characterEpisodes),
                characterUrl = characterUrl,
                characterCreated = characterCreated
            )
        }
    }
}