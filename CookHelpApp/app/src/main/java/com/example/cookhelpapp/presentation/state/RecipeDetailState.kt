package com.example.cookhelpapp.presentation.state

import com.example.cookhelpapp.domain.model.RecipeDetailed

/**
 * Representa el estado inmutable de la pantalla de detalle de receta.
 *
 * @property isLoading Indica si los detalles de la receta se están cargando.
 * @property recipe Los detalles de la receta ([RecipeDetailed]), o null si aún no se cargan o hay error.
 * @property error Mensaje de error si la carga falló, o null si no hay error.
 * @property isFavorite Indica si la receta actualmente mostrada está marcada como favorita.
 */
data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val recipe: RecipeDetailed? = null,
    val error: String? = null,
    val isFavorite: Boolean = false
)
