package com.liviolopez.citycrimes.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "availability")
data class Availability(
    @PrimaryKey val date: String
)