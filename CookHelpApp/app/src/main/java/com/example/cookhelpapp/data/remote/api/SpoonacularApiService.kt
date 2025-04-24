package com.example.cookhelpapp.data.remote.api

import com.example.cookhelpapp.data.remote.dto.ComplexSearchResponseDto
import com.example.cookhelpapp.data.remote.dto.FindByIngredientsDto
import com.example.cookhelpapp.data.remote.dto.RecipeDetailedDto

/**
 * Define el contrato para interactuar con la API de Spoonacular.
 * Cada método corresponde a un endpoint específico y devuelve el resultado
 * encapsulado en un Result<T>, indicando éxito (con el DTO) o fallo (con Throwable).
 */
interface SpoonacularApiService {
    /**
     * Busca recetas priorizando una lista de ingredientes.
     * Llama al endpoint /findByIngredients.
     * @param includeIngredients Lista de ingredientes a usar (requerido).
     * @param sortBy Criterio para ordenar/rankear (max-used o min-missing). Opcional.
     * @param number Número máximo de resultados.
     * @param offset Número de resultados a saltar (informativo, la API puede ignorarlo).
     * @return Un Result que encapsula una Lista de FindByIngredientsItemDto o un Throwable.
     */
    suspend fun buscarPorIngredientes(
        includeIngredients: List<String>,
        ranking: Int,           // 1 para max-used, 2 para min-missing
        number: Int = 50,       // Número predeterminado máximo de resultados
        offset: Int = 10        // Número de elementos a saltar (paginación)
    ): Result<List<FindByIngredientsDto>>

    /**
     * Realiza una búsqueda compleja de recetas con varios filtros y paginación.
     * Llama al endpoint /recipes/complexSearch.
     * @param query Consulta de búsqueda general (opcional).
     * @param includeIngredients Lista de ingredientes que deben estar (opcional).
     * @param cuisine Filtro por tipo de cocina (opcional).
     * @param diet Filtro por tipo de dieta (opcional).
     * @param offset Número de resultados a saltar (paginación).
     * @param number Número máximo de resultados a devolver.
     * @return Un Result que encapsula ComplexSearchResponseDto o un Throwable.
     */
    suspend fun buscarComplejo(
        query: String? = null,
        includeIngredients: List<String>? = null,
        cuisine: String? = null,
        diet: String? = null,
        number: Int = 50,
        offset: Int = 10
    ): Result<ComplexSearchResponseDto>

    /**
     * Obtiene detalles completos de una receta por su ID.
     * Llama al endpoint /recipes/{id}/information.
     * @param id El ID único de la receta.
     * @return Un Result que encapsula RecipeDetailedDto o un Throwable.
     */
    suspend fun obtenerDetallesReceta(id: Int): Result<RecipeDetailedDto>
}