package com.example.cookhelpapp.domain.model

data class RecipeDetailed(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val instructions: String?,
    val readyInMinutes: Int?,
    val servings: Int?,
    val cuisines: List<String>?,
    val ingredients: List<IngredientDetailed>
)