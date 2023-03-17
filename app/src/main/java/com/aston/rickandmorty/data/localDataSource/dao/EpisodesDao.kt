package com.aston.rickandmorty.data.localDataSource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aston.rickandmorty.data.localDataSource.models.EpisodeInfoDto
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEpisode(data: EpisodeInfoDto)

    @Query("SELECT * FROM EpisodesTable")
    fun getAllFromDb(): Flow<List<EpisodeInfoDto>>

    @Query("DELETE FROM EpisodesTable")
    suspend fun deleteAllEpisodesData()

    @Query("SELECT * FROM EpisodesTable WHERE episodeId == :id LIMIT 1")
    suspend fun getEpisode(id: Int): EpisodeInfoDto?
}