package com.example.cookhelpapp.domain.repository

import com.example.cookhelpapp.domain.model.RecipeDetailed
import com.example.cookhelpapp.domain.model.RecipeSummary
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define las operaciones disponibles para obtener y gestionar datos de recetas.
 */
interface RecipeRepository {

    /**
     * Realiza una búsqueda compleja en la API remota.
     *
     * @param offset Paginación: número de resultados a saltar (default 0).
     * @param number Paginación: número de resultados a devolver (default 20).
     * @return Un [Result] que contiene [List]<[RecipeSummary]> o un [Throwable].
     */
    suspend fun searchComplexRecipes(
        includeIngredients: List<String>? = null,
        cuisine: String? = null,
        offset: Int = 0,
        number: Int = 20
    ): Result<List<RecipeSummary>>

    /**
     * Realiza una búsqueda por ingredientes en la API remota.
     * @return Result con la lista simple de RecipeSummary o un error.
     */
    suspend fun searchRecipesByIngredients(
        includeIngredients: List<String>?,
        ranking: Int,
        offset: Int = 0,
        number: Int = 20
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
     * Obtiene un [Flow] que emite `true` si la receta con el [id] especificado
     * es favorita (existe localmente), y `false` si no lo está.
     * El Flow emitirá un nuevo valor booleano si el estado de favorito cambia.
     *
     * @param id El ID de la receta a comprobar.
     * @return Un [Flow] que emite [Boolean].
     */
    fun isFavoriteStream(id: Int): Flow<Boolean>


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