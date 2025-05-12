package com.example.cookhelpapp.domain.usecase

import com.example.cookhelpapp.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de Uso para obtener un flujo (stream) que indica si una receta es favorita.
 * Delega la llamada al repositorio.
 *
 * @property repository El repositorio de recetas inyectado.
 */
class IsFavoriteStreamUseCase(
    private val repository: RecipeRepository
) {
    /**
     * Obtiene el Flow que indica el estado de favorito.
     * @param id El ID de la receta a comprobar.
     * @return Un [Flow] que emite `true` si es favorita, `false` si no.
     */
    operator fun invoke(id: Int): Flow<Boolean> {
        // Llama al método correspondiente del repositorio.
        return repository.isFavoriteStream(id)
    }
}
