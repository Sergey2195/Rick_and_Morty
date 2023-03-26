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
        return with(src){
            LocationModel(
                id = locationId ?: 0,
                name = locationName ?: "",
                type = locationType ?: "",
                dimension = locationDimension ?: ""
            )
        }
    }

    fun transformListLocationInfoRemoteIntoListLocationModel(src: List<LocationInfoRemote>): List<LocationModel> {
        return src.map { transformLocationInfoRemoteIntoLocationModel(it) }
    }

    fun transformLocationInfoRemoteInfoLocationDetailsModelWithIds(src: LocationInfoRemote): LocationDetailsModelWithId {
        return with(src){
            LocationDetailsModelWithId(
                locationId = locationId ?: 0,
                locationName = locationName ?: "",
                locationType = locationType ?: "",
                dimension = locationDimension ?: "",
                characters = locationResidents?.map { utils.getLastIntAfterSlash(it) ?: 0 }
                    ?: emptyList()
            )
        }
    }

    fun transformLocationDtoIntoLocationDetailsWithIds(src: LocationInfoDto): LocationDetailsModelWithId {
        return with(src){
            LocationDetailsModelWithId(
                locationId = locationId ?: 0,
                locationName = locationName ?: "",
                locationType = locationType ?: "",
                dimension = locationDimension ?: "",
                characters = utils.transformStringIdIntoListInt(locationResidentsIds)
            )
        }
    }

    fun transformLocationInfoDtoIntoLocationInfoRemote(src: LocationInfoDto): LocationInfoRemote {
        return with(src){
            LocationInfoRemote(
                locationId = locationId,
                locationName = locationName,
                locationType = locationType,
                locationDimension = locationDimension,
                locationResidents = utils.transformStringIdToList(locationResidentsIds),
                locationUrl = locationUrl,
                locationCreated = locationCreated
            )
        }
    }


    fun transformLocationInfoRemoteIntoLocationInfoDto(src: LocationInfoRemote): LocationInfoDto {
        return with(src){
            LocationInfoDto(
                locationId = locationId,
                locationName = locationName,
                locationType = locationType,
                locationDimension = locationDimension,
                locationResidentsIds = utils.transformListStringsToIds(locationResidents),
                locationUrl = locationUrl,
                locationCreated = locationCreated
            )
        }
    }
}