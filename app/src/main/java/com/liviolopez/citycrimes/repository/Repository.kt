package com.liviolopez.citycrimes.repository

import com.liviolopez.citycrimes.data.local.model.*
import com.liviolopez.citycrimes.data.remote.response.DetailsDto
import com.liviolopez.citycrimes.utils.Resource
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun initializeCache()

    fun fetchCrimes(date: String, categoryId: String, forceId: String, location: Crime.Location? = null): Flow<Resource<List<CrimeInfo>>>

    fun getCrime(persistentId: String): Flow<CrimeInfo>
    suspend fun fetchCrimeDetail(persistentId: String): Flow<Resource<DetailsDto>>

    fun getAvailabilities(): Flow<List<Availability>>
    fun getCategories(): Flow<List<Category>>
    fun getForces(): Flow<List<Force>>
}