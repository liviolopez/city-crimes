package com.liviolopez.citycrimes.data.remote.response

import com.liviolopez.citycrimes.data.local.model.Force

data class ForceDto(
    val id: String,
    val name: String
)

fun ForceDto.toLocalModel() = Force(
    id = id,
    name = name
)