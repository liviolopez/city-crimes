package com.liviolopez.citycrimes.data.local.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CrimeDao {
    @Transaction
    @Query("SELECT * FROM crime WHERE persistentId = :persistentId")
    fun getCrime(persistentId: String): Flow<List<CrimeInfo>>

    @Transaction
    @Query("SELECT * FROM crime WHERE month = :date AND categoryId = :categoryId AND forceId = :forceId")
    fun getCrimes(date: String, categoryId: String, forceId: String): Flow<List<CrimeInfo>>

    @Transaction
    @Query("SELECT * FROM crime WHERE month = :date AND forceId = :forceId ")
    fun getCrimesAllCategories(date: String, forceId: String): Flow<List<CrimeInfo>>

    @Transaction
    @Query("SELECT * FROM crime WHERE month = :date AND categoryId = :categoryId AND forceId = :forceId AND closeToMe = :closeToMe")
    fun getCrimesCloseToMe(date: String, categoryId: String, forceId: String, closeToMe: Boolean): Flow<List<CrimeInfo>>

    @Transaction
    @Query("SELECT * FROM crime WHERE month = :date AND forceId = :forceId AND closeToMe = :closeToMe")
    fun getCrimesCloseToMeAllCategories(date: String, forceId: String, closeToMe: Boolean): Flow<List<CrimeInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrimes(crimes: List<Crime>)

    @Query("DELETE FROM Crime")
    suspend fun deleteAllCrimes()
}