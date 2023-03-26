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
@Inject constructor(private val utils: Utils){

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

    fun transformEpisodeInfoRemoteIntoEpisodeInfoDto(src: EpisodeInfoRemote): EpisodeInfoDto {
        return EpisodeInfoDto(
            episodeId = src.episodeId,
            episodeName = src.episodeName,
            episodeAirDate = src.episodeAirDate,
            episodeNumber = src.episodeNumber,
            episodeCharacters = utils.transformListStringsToIds(src.episodeCharacters),
            episodeUrl = src.episodeUrl,
            episodeCreated = src.episodeCreated
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

    fun transformEpisodeInfoDtoIntoEpisodeInfoRemote(src: EpisodeInfoDto): EpisodeInfoRemote {
        return EpisodeInfoRemote(
            episodeId = src.episodeId,
            episodeName = src.episodeName,
            episodeAirDate = src.episodeAirDate,
            episodeNumber = src.episodeNumber,
            episodeCharacters = utils.transformStringIdToList(src.episodeCharacters),
            episodeUrl = src.episodeUrl,
            episodeCreated = src.episodeCreated
        )
    }
}