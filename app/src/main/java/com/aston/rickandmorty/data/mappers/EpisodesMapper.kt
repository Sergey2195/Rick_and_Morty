package com.aston.rickandmorty.data.mappers

import com.aston.rickandmorty.data.localDataSource.models.EpisodeInfoDto
import com.aston.rickandmorty.data.remoteDataSource.models.EpisodeInfoRemote
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.domain.entity.EpisodeDetailsModel
import com.aston.rickandmorty.domain.entity.EpisodeModel
import com.aston.rickandmorty.utils.Utils
import javax.inject.Inject

@ApplicationScope
class EpisodesMapper
@Inject constructor(private val utils: Utils) {

    fun transformListEpisodeInfoRemoteIntoListEpisodeModel(src: List<EpisodeInfoRemote>): List<EpisodeModel> {
        return src.map { transformEpisodeInfoRemoteIntoEpisodeModel(it) }
    }

    fun transformEpisodeInfoRemoteIntoEpisodeModel(src: EpisodeInfoRemote): EpisodeModel {
        return with(src) {
            EpisodeModel(
                id = episodeId ?: 0,
                name = episodeName ?: "",
                number = episodeNumber ?: "",
                dateRelease = episodeAirDate ?: ""
            )
        }
    }

    fun transformEpisodeInfoRemoteIntoEpisodeInfoDto(src: EpisodeInfoRemote): EpisodeInfoDto {
        return with(src) {
            EpisodeInfoDto(
                episodeId = episodeId,
                episodeName = episodeName,
                episodeAirDate = episodeAirDate,
                episodeNumber = episodeNumber,
                episodeCharacters = utils.transformListStringsToIds(episodeCharacters),
                episodeUrl = episodeUrl,
                episodeCreated = episodeCreated
            )
        }
    }

    fun configurationEpisodeDetailsModel(
        episode: EpisodeInfoRemote?,
        characters: List<CharacterModel>
    ): EpisodeDetailsModel? {
        if (episode == null) return null
        return with(episode) {
            EpisodeDetailsModel(
                id = episodeId ?: 0,
                name = episodeName ?: "",
                airDate = episodeAirDate ?: "",
                episodeNumber = episodeNumber ?: "",
                characters = characters
            )
        }
    }

    fun transformEpisodeInfoDtoIntoEpisodeInfoRemote(src: EpisodeInfoDto): EpisodeInfoRemote {
        return with(src) {
            EpisodeInfoRemote(
                episodeId = episodeId,
                episodeName = episodeName,
                episodeAirDate = episodeAirDate,
                episodeNumber = episodeNumber,
                episodeCharacters = utils.transformStringIdToList(episodeCharacters),
                episodeUrl = episodeUrl,
                episodeCreated = episodeCreated
            )
        }
    }
}