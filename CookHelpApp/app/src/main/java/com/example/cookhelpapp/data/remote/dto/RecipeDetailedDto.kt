package com.example.cookhelpapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetailedDto(
    val id: Int,
    val title: String,
    @SerialName("image")
    val imageUrl: String?,
    val cuisines: List<String> = mutableListOf(),
    @SerialName("extendedIngredients")
    val ingredients: List<IngredientInfoDto>,
    val instructions: String?,
    val readyInMinutes: Int,
    val servings: Int
)