package com.liviolopez.citycrimes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
@TypeConverters(RoomTypeConverters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun crimeDao(): CrimeDao
    abstract fun forceDao(): ForceDao
    abstract fun categoryDao(): CategoryDao
    abstract fun availabilityDao(): AvailabilityDao
}

class RoomTypeConverters {
    @TypeConverter
    fun toLocation(location: String?): Crime.Location? {
        if(location.isNullOrEmpty()) return null
        return try { Gson().fromJson(location, object: TypeToken<Crime.Location>() {}.type) }
               catch (e: Throwable) { null }
    }

    @TypeConverter
    fun fromLocation(location: Crime.Location?): String? {
        if(location == null) return null
        return try { Gson().toJson(location) }
               catch (e: Throwable) { null }
    }
}