// data/remote/api/SpoonacularApiService.kt
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
     * Usa valores por defecto para number y offset si no se especifican.
     * @param includeIngredients Lista de ingredientes a usar (requerido).
     * @param ranking Criterio numérico (1 para max-used, 2 para min-missing).
     * @param number Número máximo de resultados.
     * @param offset Número de resultados a saltar (la API /findByIngredients puede ignorarlo).
     * @return Un Result que encapsula una Lista de FindByIngredientsDto o un Throwable.
     */
    suspend fun getRecipesByIngredients(
        includeIngredients: List<String>,
        ranking: Int,           // 1 o 2, sin valor por defecto, debe proporcionarse
        number: Int = 20,
        offset: Int = 0
    ): Result<List<FindByIngredientsDto>>

    /**
     * Realiza una búsqueda compleja de recetas con filtros opcionales.
     * Llama al endpoint /recipes/complexSearch.
     * Usa valores por defecto para number y offset si no se especifican.
     * @param includeIngredients Lista opcional de ingredientes que deben estar.
     * @param cuisine Filtro opcional por tipo de cocina.
     * @param number Número máximo de resultados.
     * @param offset Número de resultados a saltar.
     * @return Un Result que encapsula ComplexSearchResponseDto o un Throwable.
     */
    suspend fun complexSearchRecipes(
        // query y diet eliminados
        includeIngredients: List<String>? = null,
        cuisine: String? = null,
        number: Int = 20,
        offset: Int = 0
    ): Result<ComplexSearchResponseDto>

    /**
     * Obtiene detalles completos de una receta por su ID.
     * Llama al endpoint /recipes/{id}/information.
     * @param id El ID único de la receta.
     * @return Un Result que encapsula RecipeDetailedDto o un Throwable.
     */
    suspend fun fetchRecipeDetails(id: Int): Result<RecipeDetailedDto>
}