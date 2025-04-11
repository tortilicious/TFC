package com.example.cookhelpapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RecetaDetalleDto(
    val id: Int,
    val title: String,
    val image: String?,
    @SerialName("extendedIngredients")
    val ingredients: List<IngredienteDto>,
    val instructions: String?,
    val readyInMinutes: Int,
    val servings: Int
)
