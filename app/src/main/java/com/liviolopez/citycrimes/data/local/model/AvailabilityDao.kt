package com.liviolopez.citycrimes.data.local.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AvailabilityDao {
    @Query("SELECT * FROM availability")
    fun getAvailabilities(): Flow<List<Availability>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvailabilities(availability: List<Availability>)

    @Query("DELETE FROM availability")
    suspend fun deleteAllAvailabilities()

    @Query("SELECT COUNT(*) FROM availability")
    suspend fun getCountAvailabilities(): Int
}