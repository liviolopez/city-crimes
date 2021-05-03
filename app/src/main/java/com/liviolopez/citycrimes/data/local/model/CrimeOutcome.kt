package com.liviolopez.citycrimes.data.local.model

import androidx.room.*

@Entity(
    tableName = "crimeOutcomes",
    foreignKeys = [
        ForeignKey(
            entity = Crime::class,
            parentColumns = ["id"],
            childColumns = ["crimeId"]
        ),
        ForeignKey(
            entity = OutcomeStatus::class,
            parentColumns = ["id"],
            childColumns = ["outcomeStatusId"]
        ),
    ],
    indices = [
        Index(value = ["crimeId"], unique = false),
        Index(value = ["outcomeStatusId"], unique = false)
    ]
)
data class CrimeOutcome(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val crimeId: Int,
    val outcomeStatusId: String,
    val month: String,
    val isLatest: Boolean
)

@DatabaseView( value =
    "SELECT " +
        "_c.crimeId, " +
        "_c.month, " +
        "_c.isLatest, " +
        "_o.name AS description " +
    "FROM crimeOutcomes AS _c LEFT JOIN outcomeStatus AS _o " +
        "ON _o.id = _c.outcomeStatusId")
data class CrimeOutcomeInfo(
    val crimeId: Int,
    val month: String,
    val isLatest: Boolean,
    val description: String
)