package com.liviolopez.citycrimes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.liviolopez.citycrimes.data.local.model.*

@Database(
    entities = [
        Crime::class,
        Force::class,
        Category::class,
        Availability::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun crimeDao(): CrimeDao
    abstract fun forceDao(): ForceDao
    abstract fun categoryDao(): CategoryDao
    abstract fun availabilityDao(): AvailabilityDao
}