package com.liviolopez.citycrimes.data.local.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CrimeOutcomeDao {
    @Query("SELECT * FROM crimeOutcomes WHERE crimeId = :crimeId ORDER BY month, isLatest")
    fun getCrimeOutcomes(crimeId: Int): Flow<List<CrimeOutcome>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrimeOutcomes(crimeOutcomes: List<CrimeOutcome>)

    @Query("DELETE FROM crimeOutcomes WHERE crimeId = :crimeId")
    suspend fun deleteOutcomesOfCrime(crimeId: Int)

    @Query("DELETE FROM crimeOutcomes")
    suspend fun deleteAllCrimeOutcomes()
}