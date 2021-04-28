package com.liviolopez.citycrimes.data.remote.response

import com.liviolopez.citycrimes.data.local.model.Category

data class CategoryDto(
    val url: String?,
    val code: String?,
    val name: String
)

fun CategoryDto.toLocalModel() = Category(
    id = url ?: code ?: "",
    name = name
)