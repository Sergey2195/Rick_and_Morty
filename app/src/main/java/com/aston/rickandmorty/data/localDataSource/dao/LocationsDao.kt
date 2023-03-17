package com.aston.rickandmorty.data.localDataSource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aston.rickandmorty.data.localDataSource.models.LocationInfoDto
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLocation(data: LocationInfoDto)

    @Query("SELECT * FROM LocationsTable")
    fun getAllFromDb(): Flow<List<LocationInfoDto>>

    @Query("DELETE FROM LocationsTable")
    suspend fun deleteAllLocationsData()

    @Query("SELECT * FROM LocationsTable WHERE locationId == :id LIMIT 1")
    suspend fun getSingleLocation(id: Int): LocationInfoDto?

    @Query("SELECT * FROM LocationsTable WHERE locationId == :id LIMIT 1")
    fun getSingleLocationRx(id: Int): Single<LocationInfoDto>
}