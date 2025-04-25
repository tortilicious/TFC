package com.example.cookhelpapp.domain.usecase

import com.example.cookhelpapp.domain.model.RecipeSummary
import com.example.cookhelpapp.domain.repository.RecipeRepository
import kotlin.Result

/**
 * Caso de Uso para ejecutar la búsqueda compleja de recetas.
 * Delega la llamada al repositorio.
 *
 * @property repository El repositorio inyectado mediante Koin.
 */
class SearchComplexRecipesUseCase(
    private val repository: RecipeRepository
) {
    /**
     * Ejecuta la búsqueda compleja.
     * @param includeIngredients Lista opcional de ingredientes.
     * @param cuisine Filtro opcional por tipo de cocina.
     * @param offset Paginación: inicio.
     * @param number Paginación: cantidad.
     * @return Un [Result] con la lista de [RecipeSummary] o un error.
     */
    suspend operator fun invoke(
        includeIngredients: List<String>? = null,
        cuisine: String? = null,
        offset: Int = 0,
        number: Int = 20
    ): Result<List<RecipeSummary>> {
        return repository.searchComplexRecipes(includeIngredients, cuisine, offset, number)
    }
}