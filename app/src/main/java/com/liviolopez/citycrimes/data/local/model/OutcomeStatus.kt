package com.liviolopez.citycrimes.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The methods related to the DB table "OutcomesStatus" are a clear example of Overplanning.
 *
 * However these methods were developed as examples of:
 * - Room Entity/Table with multiple foreignKeys (CrimeOutcome entity)
 * - Room @DatabaseView and one-to-many @Relation with @Entity
 * - Fetch data from a locally stored JSON file
 */
@Entity(tableName = "outcomeStatus")
data class OutcomeStatus(
    @PrimaryKey val id: String,
    val name: String
)