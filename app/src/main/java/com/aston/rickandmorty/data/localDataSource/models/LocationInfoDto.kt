package com.aston.rickandmorty.data.localDataSource.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocationsTable")
data class LocationInfoDto(
    @PrimaryKey
    val locationId: Int? = null,
    val locationName: String? = null,
    val locationType: String? = null,
    val locationDimension: String? = null,
    val locationResidentsIds: String? = null,
    val locationUrl: String? = null,
    val locationCreated: String? = null
)