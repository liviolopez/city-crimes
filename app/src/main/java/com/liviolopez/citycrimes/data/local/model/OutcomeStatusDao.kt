package com.liviolopez.citycrimes.data.local.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * The methods related to the DB table "OutcomesStatus" are a clear example of Overplanning.
 *
 * However these methods were developed as examples of:
 * - Room Entity/Table with multiple foreignKeys (CrimeOutcome entity)
 * - Room @DatabaseView and one-to-many @Relation with @Entity
 * - Fetch data from a locally stored JSON file
 */
@Dao
interface OutcomeStatusDao {
    @Query("SELECT * FROM outcomeStatus ORDER BY name")
    fun getOutcomesStatus(): List<OutcomeStatus>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutcomeStatus(outcomesStatus: List<OutcomeStatus>)

    @Query("DELETE FROM outcomeStatus")
    suspend fun deleteAllOutcomesStatus()

    @Query("SELECT COUNT(*) FROM outcomeStatus")
    suspend fun getCountOutcomesStatus(): Int
}