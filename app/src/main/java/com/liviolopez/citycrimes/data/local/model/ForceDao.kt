package com.liviolopez.citycrimes.data.local.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ForceDao {
    @Query("SELECT * FROM force ORDER BY name")
    fun getForces(): Flow<List<Force>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForces(categories: List<Force>)

    @Query("DELETE FROM force")
    suspend fun deleteAllForces()

    @Query("SELECT COUNT(*) FROM force")
    suspend fun getCountForces(): Int
}