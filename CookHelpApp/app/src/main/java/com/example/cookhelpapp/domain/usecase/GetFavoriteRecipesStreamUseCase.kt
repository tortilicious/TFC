// domain/usecase/GetFavoriteRecipesStreamUseCase.kt
package com.example.cookhelpapp.domain.usecase

import com.example.cookhelpapp.domain.model.RecipeSummary
import com.example.cookhelpapp.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de Uso para obtener el flujo (stream) de recetas favoritas locales.
 *
 * @property repository El repositorio de recetas inyectado.
 */
class GetFavoriteRecipesStreamUseCase(
    private val repository: RecipeRepository
) {
    /**
     * Obtiene el Flow de la lista de favoritos.
     * @return Un [Flow] que emite la lista actualizada de [RecipeSummary] favoritas.
     */
    operator fun invoke(): Flow<List<RecipeSummary>> {
        return repository.getFavoriteRecipesStream()
    }
}