package com.example.cookhelpapp.domain.model


/**
 * Modelo de dominio para representar la información esencial de una receta
 * en listas o previsualizaciones.
 */
data class RecipeSummary(
    val id: Int,
    val title: String,
    val imageUrl: String?
)
