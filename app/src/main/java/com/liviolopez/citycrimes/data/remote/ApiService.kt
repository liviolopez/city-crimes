package com.liviolopez.citycrimes.data.remote

import com.liviolopez.citycrimes.data.remote.response.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// https://data.police.uk/api/crime-last-updated
// https://data.police.uk/api/crimes-street-dates
// https://data.police.uk/api/forces
// https://data.police.uk/api/crime-categories
// https://data.police.uk/api/crimes-no-location?category=all-crime&force=leicestershire&date=2021-02
// https://data.police.uk/api/outcomes-for-crime/{persistentId}
// https://data.police.uk/api/crimes-street/all-crime?lat=52.629729&lng=-1.131592&date=2021-02

interface ApiService {

    @GET("api/crimes-street-dates")
    suspend fun fetchAvailabilities(): List<AvailabilityDto>

    @GET("api/crime-categories")
    suspend fun fetchCategories(): List<CategoryDto>

    @GET("api/forces")
    suspend fun fetchForces(): List<ForceDto>

    @GET("api/crimes-no-location")
    suspend fun fetchCrimes(
        @Query("date") date: String,
        @Query("category") categoryId: String,
        @Query("force") forceId: String
    ): List<CrimeDto>

    @GET("api/crimes-street/all-crime")
    suspend fun fetchCrimesCloseToMe(
        @Query("date") date: String,
        @Query("category") categoryId: String,
        @Query("force") forceId: String,
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double
    ): List<CrimeDto>

    @GET("api/outcomes-for-crime/{persistentId}")
    suspend fun fetchCrime(
        @Path("persistentId", encoded = true) persistentId: String,
    ): DetailsDto
}