package com.example.cookhelpapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class IngredienteInfoDto(
    val id: Int,
    val amount: Double,
    val unit: String,
    val name: String
)
