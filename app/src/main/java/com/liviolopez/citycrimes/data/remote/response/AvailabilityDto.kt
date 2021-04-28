package com.liviolopez.citycrimes.data.remote.response

import com.liviolopez.citycrimes.data.local.model.Availability

data class AvailabilityDto (
    val date: String
)

fun AvailabilityDto.toLocalModel() = Availability(
    date = date
)