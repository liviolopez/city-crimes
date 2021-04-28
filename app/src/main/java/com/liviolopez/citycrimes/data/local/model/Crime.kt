package com.liviolopez.citycrimes.data.local.model

import androidx.room.*

@Entity(
    tableName = "crime",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"]
        )
    ],
    indices = [Index(value = ["categoryId"], unique = false)]
)
data class Crime(
    @PrimaryKey val id: Int,
    val persistentId: String,
    val month: String,
    val categoryId: String,
    val forceId: String,
    val context: String?,

    val locationLatitude: String?,
    val locationLongitude: String?,
    val closeToMe: Boolean
)

data class CrimeInfo(
    @Embedded val crime: Crime,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    ) val category: Category
)