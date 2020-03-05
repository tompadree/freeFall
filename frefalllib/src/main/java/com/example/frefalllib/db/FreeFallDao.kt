package com.example.frefalllib.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * @author Tomislav Curis
 */

@Dao
interface FreeFallDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveFallObject(fallObject: FallObject): Long

    @Query("SELECT * FROM fallObjects")
    fun getFallObjects(): List<FallObject>
}