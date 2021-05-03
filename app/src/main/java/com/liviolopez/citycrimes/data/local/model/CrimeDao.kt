package com.liviolopez.citycrimes.data.local.model

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface CrimeDao {
    @Transaction // Annotation required because CrimeInfo is a data class with @Embedded fields
    @Query("SELECT * FROM crime WHERE persistentId = :persistentId LIMIT 1")
    fun getCrime(persistentId: String): Flow<CrimeInfo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrimes(crimes: List<Crime>)

    @Query("DELETE FROM Crime")
    suspend fun deleteAllCrimes()

    /**
     * This annotation is used instead of @Query since for this use case with the
     * @Query annotation queries with "null" values should be ignored and not searched as "field == null"
     */
    @Transaction
    @RawQuery
    fun getCrimesRaw(query: SupportSQLiteQuery): Flow<List<CrimeInfo>>

    /**
     * Query maker for Crime table to then run it with @RawQuery annotation
     */
    fun createSimpleQuery(date: String, forceId: String, categoryId: String, location: Crime.Location? = null) : SimpleSQLiteQuery {
        var query = "SELECT * FROM crime WHERE month = ? AND forceId = ?"
        val params = mutableListOf<Any>(date, forceId)

        if(categoryId != "all-crime"){
            query += " AND categoryId = ?"
            params.add(categoryId)
        }

        if(location != null){
            query += " AND wasCloseToMe = ?"
            params.add(true)
        }

        return SimpleSQLiteQuery(query, params.toTypedArray())
    }
}