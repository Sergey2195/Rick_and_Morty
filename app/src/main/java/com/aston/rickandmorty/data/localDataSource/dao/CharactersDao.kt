package com.aston.rickandmorty.data.localDataSource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aston.rickandmorty.data.localDataSource.models.CharacterInfoDto
import kotlinx.coroutines.flow.Flow

@Dao
interface CharactersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCharacter(data: CharacterInfoDto)

    @Query("SELECT * FROM CharactersTable")
    fun getAllFromDb(): Flow<List<CharacterInfoDto>>

    @Query("SELECT * FROM CharactersTable WHERE characterId == :id LIMIT 1")
    suspend fun getSingleCharacter(id: Int): CharacterInfoDto?
}