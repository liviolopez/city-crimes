package com.liviolopez.citycrimes.data.remote

import com.liviolopez.citycrimes.data.remote.response.*
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val apiService: ApiService) {
    suspend fun fetchCrimes(date: String, categoryId: String, forceId: String): List<CrimeDto> {
        return apiService.fetchCrimes(date, categoryId, forceId)
    }

    suspend fun fetchCrimesCloseToMe(date: String, categoryId: String, forceId: String, latitude: Double, longitude: Double): List<CrimeDto> {
        return apiService.fetchCrimesCloseToMe(date, categoryId, forceId, latitude, longitude)
    }

    suspend fun fetchCrimeDetails(persistentId: String): DetailsDto {
        return apiService.fetchCrime(persistentId)
    }

    suspend fun fetchAvailabilities(): List<AvailabilityDto> {
        return apiService.fetchAvailabilities()
    }

    suspend fun fetchCategories(): List<CategoryDto> {
        return apiService.fetchCategories()
    }

    suspend fun fetchForces(): List<ForceDto> {
        return apiService.fetchForces()
    }
}