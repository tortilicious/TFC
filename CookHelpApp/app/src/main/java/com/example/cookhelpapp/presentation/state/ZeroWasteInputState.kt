package com.example.cookhelpapp.presentation.state

/**
 * Representa el estado inmutable de la pantalla de entrada de filtros
 * para la búsqueda de "Aprovechamiento de Ingredientes".
 *
 * @property currentIngredientInput El texto actual en el campo para añadir un nuevo ingrediente.
 * @property selectedIngredients La lista de ingredientes que el usuario ha añadido para la búsqueda.
 * @property selectedRanking El criterio de ranking seleccionado por el usuario (1 o 2).
 * 1 = Maximizar ingredientes usados.
 * 2 = Minimizar ingredientes nuevos/faltantes.
 */
data class ZeroWasteInputUiState(
    val currentIngredientInput: String = "",
    val selectedIngredients: List<String> = emptyList(),
    val selectedRanking: Int = 1 // Valor por defecto, coincide con la primera opción del Composable
)
