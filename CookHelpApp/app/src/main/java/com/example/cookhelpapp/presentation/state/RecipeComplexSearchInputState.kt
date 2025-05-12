package com.example.cookhelpapp.presentation.state

/**
 * Representa el estado inmutable de la pantalla de búsqueda de recetas ("Nuevas Recetas").
 * Contiene los filtros del usuario, los resultados y el estado de carga/paginación.
 */
data class RecipeComplexSearchInputState(
    // --- Estado de los Inputs del Usuario ---
    /** El texto actual en el campo de texto para añadir un nuevo ingrediente. */
    val currentIngredientInput: String = "",
    /** La lista de ingredientes que el usuario ha añadido para la búsqueda. */
    val selectedIngredients: List<String> = emptyList(),
    /** La lista de tipos de cocina disponibles para seleccionar (podría cargarse de otro sitio). */
    val availableCuisines: List<String> = listOf(
        "Italian",
        "Mexican",
        "Chinese",
        "Indian",
        "Spanish",
        "French",
        "Japanese",
        "Thai",
        "Greek",
        "American",
        "European",
        "Vietnamese",
        "Nordic"
    ),
    /** La cocina seleccionada por el usuario como filtro (null si no hay selección). */
    val selectedCuisine: String? = null
)
