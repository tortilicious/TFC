// domain/usecase/GetLocalFavoriteRecipeDetailsStreamUseCase.kt
package com.example.cookhelpapp.domain.usecase

import com.example.cookhelpapp.domain.model.RecipeDetailed
import com.example.cookhelpapp.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de Uso para obtener el flujo (stream) de los detalles de una receta favorita LOCAL.
 *
 * @property repository El repositorio de recetas inyectado.
 */
class GetLocalFavoriteRecipeDetailsStreamUseCase(
    private val repository: RecipeRepository
) {
    /**
     * Obtiene el Flow con los detalles locales de un favorito.
     * @param id El ID de la receta favorita.
     * @return Un [Flow] que emite [RecipeDetailed?] (nulable si no se encuentra o se borra).
     */
    operator fun invoke(id: Int): Flow<RecipeDetailed?> {
        return repository.getLocalFavoriteRecipeDetailsStream(id)
    }
}