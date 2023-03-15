package com.aston.rickandmorty.data.localDataSource

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CharactersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCharacter(data: CharacterInfoDto)

    @Query("SELECT * FROM CharactersTable")
    suspend fun getAllFromDb(): List<CharacterInfoDto>
}