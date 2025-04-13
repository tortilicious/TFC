package com.example.cookhelpapp.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class FindByIngredientsDto(
    val id: Int,
    val title: String,
    val image: String?,
    val missedIngredientCount: Int,
    val missedIngredients: List<IngredienteInfoDto> = emptyList()
)
