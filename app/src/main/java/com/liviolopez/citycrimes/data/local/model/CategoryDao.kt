package com.liviolopez.citycrimes.data.local.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category ORDER BY name")
    fun getCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Query("DELETE FROM category")
    suspend fun deleteAllCategories()

    @Query("SELECT COUNT(*) FROM category")
    suspend fun getCountCategories(): Int
}