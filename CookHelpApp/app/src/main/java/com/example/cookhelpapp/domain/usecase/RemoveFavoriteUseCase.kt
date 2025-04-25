// domain/usecase/RemoveFavoriteUseCase.kt
package com.example.cookhelpapp.domain.usecase

import com.example.cookhelpapp.domain.repository.RecipeRepository
import kotlin.Result

/**
 * Caso de Uso para eliminar una receta de favoritos (borrarla localmente).
 *
 * @property repository El repositorio de recetas inyectado.
 */
class RemoveFavoriteUseCase(
    private val repository: RecipeRepository
) {
    /**
     * Elimina el favorito por ID.
     * @param id El ID de la receta a eliminar.
     * @return Un [Result] indicando éxito ([Unit]) o fallo ([Throwable]).
     */
    suspend operator fun invoke(id: Int): Result<Unit> {
        return repository.removeFavorite(id)
    }
}