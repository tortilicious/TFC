package com.example.cookhelpapp.presentation.state

import com.example.cookhelpapp.domain.model.RecipeSummary

/**
 * Representa el estado inmutable de la pantalla que muestra los resultados de búsqueda de recetas.
 *
 * @property isLoadingInitial Indica si la carga inicial (primera página) está en progreso.
 * @property isLoadingMore Indica si se está cargando una página subsiguiente (paginación).
 * @property recipes La lista acumulada de resúmenes de recetas [RecipeSummary] a mostrar.
 * @property error Mensaje de error a mostrar si alguna operación falló, o null si no hay error.
 * @property canLoadMore Verdadero si hay potencialmente más páginas para cargar.
 * @property currentOffset El offset (índice del primer item) a usar para solicitar la *siguiente* página.
 * @property numberPerPage Cuántos items se piden por página.
 * @property searchType El tipo de búsqueda que originó estos resultados (ej: "complex", "byIngredients").
 * @property initialIngredients Los ingredientes usados para esta búsqueda (si aplica).
 * @property initialCuisine La cocina usada para esta búsqueda (si aplica).
 * @property initialRanking El ranking usado para esta búsqueda (si aplica).
 * @property noResults Indica si la búsqueda se completó pero no arrojó resultados.
 */
data class ShowRecipesUiState(
    val isLoadingInitial: Boolean = false,
    val isLoadingMore: Boolean = false,
    val recipes: List<RecipeSummary> = emptyList(),
    val error: String? = null,
    val canLoadMore: Boolean = true,
    val currentOffset: Int = 0,
    val numberPerPage: Int = 20,
    // Filtros con los que se inició esta pantalla de resultados
    val searchType: String = "",
    val initialIngredients: List<String>? = null,
    val initialCuisine: String? = null,
    val initialRanking: Int? = null,
    val noResults: Boolean = false // Para mostrar un mensaje si la lista está vacía después de cargar
)
