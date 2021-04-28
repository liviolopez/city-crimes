package com.liviolopez.citycrimes.repository

import com.liviolopez.citycrimes.data.local.model.Availability
import com.liviolopez.citycrimes.data.local.model.Category
import com.liviolopez.citycrimes.data.local.model.CrimeInfo
import com.liviolopez.citycrimes.data.local.model.Force
import com.liviolopez.citycrimes.data.remote.response.DetailsDto
import com.liviolopez.citycrimes.utils.Resource
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun initializeCache()

    fun fetchCrimes(date: String, categoryId: String, forceId: String): Flow<Resource<List<CrimeInfo>>>
    fun fetchCrimesCloseToMe(date: String, categoryId: String, forceId: String, latitude: Double, longitude: Double): Flow<Resource<List<CrimeInfo>>>

    fun getCrime(persistentId: String): Flow<List<CrimeInfo>>
    suspend fun fetchCrimeDetail(persistentId: String): Flow<Resource<DetailsDto>>

    fun getAvailabilities(): Flow<List<Availability>>
    fun getCategories(): Flow<List<Category>>
    fun getForces(): Flow<List<Force>>
}