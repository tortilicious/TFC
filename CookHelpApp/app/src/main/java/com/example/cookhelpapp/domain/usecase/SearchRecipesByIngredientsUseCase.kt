package com.example.cookhelpapp.domain.usecase

import com.example.cookhelpapp.domain.model.RecipeSummary
import com.example.cookhelpapp.domain.repository.RecipeRepository
import kotlin.Result

/**
 * Caso de Uso para ejecutar la búsqueda de recetas por ingredientes.
 * Delega la llamada al repositorio.
 *
 * @property repository El repositorio de recetas inyectado mediante Koin.
 */
class SearchRecipesByIngredientsUseCase(
    private val repository: RecipeRepository
) {
    /**
     * Ejecuta la búsqueda por ingredientes.
     * @param includeIngredients Lista de ingredientes (requerido).
     * @param ranking Criterio de ordenación (1 o 2).
     * @param number Cantidad de resultados.
     * @return Un [Result] con la lista de [RecipeSummary] o un error.
     */
    suspend operator fun invoke(
        includeIngredients: List<String>,
        ranking: Int,
    ): Result<List<RecipeSummary>> {
        // require(ranking == 1 || ranking == 2)
        return repository.searchRecipesByIngredients(includeIngredients, ranking)
    }
}