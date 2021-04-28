package com.liviolopez.citycrimes.data.remote.response

data class DetailsDto(
    val crime: CrimeDto,
    val outcomes: List<Outcome>
)

data class Outcome(
    val category: CategoryDto,
    val date: String
)