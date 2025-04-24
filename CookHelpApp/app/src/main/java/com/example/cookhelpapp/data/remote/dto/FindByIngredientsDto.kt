package com.example.cookhelpapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FindByIngredientsDto(
    val id: Int,
    val title: String,
    @SerialName("image")
    val imageUrl: String?,
    val missedIngredientCount: Int,
    val missedIngredients: List<IngredientInfoDto> = emptyList()
)
