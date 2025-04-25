// domain/usecase/AddFavoriteUseCase.kt
package com.example.cookhelpapp.domain.usecase

import com.example.cookhelpapp.domain.model.RecipeDetailed
import com.example.cookhelpapp.domain.repository.RecipeRepository
import kotlin.Result

/**
 * Caso de Uso para añadir una receta a favoritos (guardarla localmente).
 *
 * @property repository El repositorio de recetas inyectado.
 */
class AddFavoriteUseCase(
    private val repository: RecipeRepository
) {
    /**
     * Guarda la receta detallada como favorita.
     * @param recipe El modelo de dominio [RecipeDetailed] a guardar.
     * @return Un [Result] indicando éxito ([Unit]) o fallo ([Throwable]).
     */
    suspend operator fun invoke(recipe: RecipeDetailed): Result<Unit> {
        // Podrías añadir validaciones de negocio aquí antes de guardar
        // Ejemplo: if(recipe.ingredients.isEmpty()) return Result.failure(...)
        return repository.addFavorite(recipe)
    }
}