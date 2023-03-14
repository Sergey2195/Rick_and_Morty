package com.aston.rickandmorty.data.localDataSource

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aston.rickandmorty.data.localDataSource.models.CharacterInfoDto

@Dao
interface CharacterDao {

    @Query("SELECT * FROM character_table WHERE id == :id LIMIT 1")
    suspend fun getCharacterInfo(id: Int): CharacterInfoDto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterInfo(characterData: CharacterInfoDto)
}