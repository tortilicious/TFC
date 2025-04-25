// domain/usecase/GetRemoteRecipeDetailsUseCase.kt
package com.example.cookhelpapp.domain.usecase

import com.example.cookhelpapp.domain.model.RecipeDetailed
import com.example.cookhelpapp.domain.repository.RecipeRepository
import kotlin.Result

/**
 * Caso de Uso para obtener los detalles completos de una receta.
 * Llama al método del repositorio que prioriza la fuente remota (API).
 *
 * @property repository El repositorio de recetas inyectado.
 */
class GetRemoteRecipeDetailsUseCase(
    private val repository: RecipeRepository
) {
    /**
     * Obtiene los detalles de la receta.
     * @param id El ID de la receta.
     * @return Un [Result] con [RecipeDetailed] o un error.
     */
    suspend operator fun invoke(id: Int): Result<RecipeDetailed> {
        return repository.getRemoteRecipeDetails(id)
    }
}