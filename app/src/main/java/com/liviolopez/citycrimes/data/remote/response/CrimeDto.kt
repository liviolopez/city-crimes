package com.liviolopez.citycrimes.data.remote.response

import com.liviolopez.citycrimes.data.local.model.Crime

data class CrimeDto(
    val id: Int,
    val persistentId: String,
    val category: String,
    val context: String?,
    val location: Location?,
    val locationSubtype: String,
    val locationType: String?,
    val month: String,
    val outcomeStatus: OutcomeStatus?,
) {
    data class OutcomeStatus(
        val category: String,
        val date: String
    )

    data class Location(
        val latitude: String,
        val longitude: String,
        val street: Street
    )

    data class Street(
        val id: Int,
        val name: String
    )
}

fun CrimeDto.toLocalModel(forceId: String, wasCloseToMe: Boolean) = Crime(
    id = id,
    persistentId = persistentId,
    month = month,
    categoryId = category,
    forceId = forceId,
    context = context,

    wasCloseToMe = wasCloseToMe,
    location = if(!location?.latitude.isNullOrEmpty() && !location?.longitude.isNullOrEmpty()) Crime.Location(location!!.latitude.toDouble(), location.longitude.toDouble()) else null
)