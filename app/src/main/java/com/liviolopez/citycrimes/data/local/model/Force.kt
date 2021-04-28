package com.liviolopez.citycrimes.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "force")
data class Force(
    @PrimaryKey val id: String,
    val name: String
)