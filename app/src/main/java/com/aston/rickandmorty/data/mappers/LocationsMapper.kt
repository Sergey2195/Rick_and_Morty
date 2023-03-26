package com.aston.rickandmorty.data.mappers

import com.aston.rickandmorty.data.localDataSource.models.LocationInfoDto
import com.aston.rickandmorty.data.remoteDataSource.models.LocationInfoRemote
import com.aston.rickandmorty.di.ApplicationScope
import com.aston.rickandmorty.domain.entity.LocationDetailsModelWithId
import com.aston.rickandmorty.domain.entity.LocationModel
import com.aston.rickandmorty.utils.Utils
import javax.inject.Inject

@ApplicationScope
class LocationsMapper
@Inject constructor(private val utils: Utils){

    fun transformLocationInfoRemoteIntoLocationModel(src: LocationInfoRemote): LocationModel {
        return LocationModel(
            id = src.locationId ?: 0,
            name = src.locationName ?: "",
            type = src.locationType ?: "",
            dimension = src.locationDimension ?: ""
        )
    }

    fun transformListLocationInfoRemoteIntoListLocationModel(src: List<LocationInfoRemote>): List<LocationModel> {
        return src.map { transformLocationInfoRemoteIntoLocationModel(it) }
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

    fun transformLocationDtoIntoLocationDetailsWithIds(src: LocationInfoDto): LocationDetailsModelWithId {
        return LocationDetailsModelWithId(
            locationId = src.locationId ?: 0,
            locationName = src.locationName ?: "",
            locationType = src.locationType ?: "",
            dimension = src.locationDimension ?: "",
            characters = utils.transformStringIdIntoListInt(src.locationResidentsIds)
        )
    }

    fun transformLocationInfoDtoIntoLocationInfoRemote(src: LocationInfoDto): LocationInfoRemote {
        return LocationInfoRemote(
            locationId = src.locationId,
            locationName = src.locationName,
            locationType = src.locationType,
            locationDimension = src.locationDimension,
            locationResidents = utils.transformStringIdToList(src.locationResidentsIds),
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
            locationResidentsIds = utils.transformListStringsToIds(src.locationResidents),
            locationUrl = src.locationUrl,
            locationCreated = src.locationCreated
        )
    }
}