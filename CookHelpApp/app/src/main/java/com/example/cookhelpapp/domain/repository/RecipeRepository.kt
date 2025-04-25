package com.example.cookhelpapp.domain.repository

import com.example.cookhelpapp.domain.model.RecipeDetailed
import com.example.cookhelpapp.domain.model.RecipeSummary
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define las operaciones disponibles para obtener y gestionar datos de recetas.
 */
interface RecipeRepository {

    /**
     * Realiza una búsqueda compleja en la fuente de datos remota (API).
     *
     * @param includeIngredients Lista opcional de ingredientes a incluir.
     * @param cuisine Filtro opcional por cocina.
     * @param offset Paginación: número de resultados a saltar.
     * @param number Paginación: número de resultados a devolver.
     * @return Un [Result] que contiene una [List] de [RecipeSummary] en caso de éxito,
     * o un [Throwable] en caso de error.
     */
    suspend fun searchComplexRecipes(
        includeIngredients: List<String>? = null,
        cuisine: String? = null,
        number: Int = 20,
        offset: Int = 0
    ): Result<List<RecipeSummary>>

    /**
     * Realiza una búsqueda por ingredientes en la API.
     *
     * @param includeIngredients Lista de ingredientes (requerido).
     * @param ranking Criterio de ordenación (1: max-used, 2: min-missing).
     * @param number Número de resultados.
     * @return Un [Result] que contiene una [List] de [RecipeSummary] en caso de éxito,
     * o un [Throwable] en caso de error.
     */
    suspend fun searchRecipesByIngredients(
        includeIngredients: List<String>,
        ranking: Int,
        number: Int = 20,
        offset: Int = 0
    ): Result<List<RecipeSummary>>

    /**
     * Obtiene los detalles completos de una receta, prioritariamente desde la API.
     * (Podría añadir lógica de caché en futuras implementaciones).
     *
     * @param id El ID de la receta.
     * @return Un [Result] que contiene [RecipeDetailed] en caso de éxito,
     * o un [Throwable] en caso de error.
     */
    suspend fun getRemoteRecipeDetails(id: Int): Result<RecipeDetailed>

    /**
     * Obtiene un [Flow] que emite continuamente la lista actualizada de recetas
     * marcadas como favoritas desde la base de datos local.
     *
     * @return Un [Flow] que emite [List] de [RecipeSummary].
     */
    fun getFavoriteRecipesStream(): Flow<List<RecipeSummary>> // Devuelve Flow de Dominio

    /**
     * Obtiene un Flow que emite los detalles completos de una receta favorita
     * **desde la base de datos local**. Emitirá null si la receta no es favorita o es eliminada.
     * @param id El ID de la receta favorita.
     * @return Un Flow que emite RecipeDetailed? (nullable).
     */
    fun getLocalFavoriteRecipeDetailsStream(id: Int): Flow<RecipeDetailed?>


    /**
     * Guarda una receta (obtenida previamente como [RecipeDetailed]) en la
     * base de datos local como favorita. Se encarga de guardar la receta,
     * los ingredientes maestros y las relaciones en una transacción.
     *
     * @param recipe El modelo de dominio [RecipeDetailed] de la receta a guardar.
     * @return Un [Result] indicando éxito ([Unit]) o fallo ([Throwable]).
     */
    suspend fun addFavorite(recipe: RecipeDetailed): Result<Unit>

    /**
     * Elimina una receta de la lista de favoritos (base de datos local) usando su ID.
     *
     * @param id El ID de la receta a eliminar de favoritos.
     * @return Un [Result] indicando éxito ([Unit]) o fallo ([Throwable]).
     */
    suspend fun removeFavorite(id: Int): Result<Unit>
}