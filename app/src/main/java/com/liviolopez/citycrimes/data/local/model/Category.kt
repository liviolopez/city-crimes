package com.liviolopez.citycrimes.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey val id: String,
    val name: String
)