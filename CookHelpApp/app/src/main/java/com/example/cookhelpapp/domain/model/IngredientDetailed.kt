package com.example.cookhelpapp.domain.model

data class IngredientDetailed(
    val id: Int, // El ID del ingrediente maestro (puede ser null si no se guardó)
    val name: String, // Nombre del ingrediente maestro
    val amount: Double?, // Cantidad usada en esta receta
    val unit: String?, // Unidad para la cantidad en esta receta
)
