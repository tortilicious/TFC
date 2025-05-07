package com.example.cookhelpapp.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.cookhelpapp.data.local.datasource.RecipeLocalDataSource
import com.example.cookhelpapp.data.local.db.CookAppDatabase
import com.example.cookhelpapp.data.local.entity.RecipeEntity
import com.example.cookhelpapp.data.local.pojo.IngredientUsageDetailsPojo
import com.example.cookhelpapp.data.mapper.toAuxRecipeIngredientEntity
import com.example.cookhelpapp.data.mapper.toIngredientEntity
import com.example.cookhelpapp.data.mapper.toRecipeDetailed
import com.example.cookhelpapp.data.mapper.toRecipeEntity
import com.example.cookhelpapp.data.mapper.toRecipeSummary
import com.example.cookhelpapp.data.remote.datasource.RecipeRemoteDataSource
import com.example.cookhelpapp.domain.model.RecipeDetailed
import com.example.cookhelpapp.domain.model.RecipeSummary
import com.example.cookhelpapp.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Implementación concreta de [RecipeRepository].
 * Orquesta las llamadas a las fuentes de datos remota ([remoteDataSource]) y local ([localDataSource]),
 * decide de dónde obtener la información y realiza los mapeos necesarios desde
 * DTOs/Entities a los Modelos de Dominio ([RecipeSummary], [RecipeDetailed]).
 *
 * @property remoteDataSource DataSource para obtener datos de la API remota de Spoonacular.
 * @property localDataSource DataSource para obtener/guardar datos en la BD local Room (favoritos).
 * @property database Instancia de la base de datos Room, necesaria para ejecutar operaciones en bloque dentro de una transacción.
 */
class RecipeRepositoryImpl(
    private val remoteDataSource: RecipeRemoteDataSource, // Inyectado mediante Koin
    private val localDataSource: RecipeLocalDataSource,   // Inyectado mediante Koin
    private val database: CookAppDatabase                 // Inyectado mediante Koin
) : RecipeRepository {


    companion object {
        private const val TAG = "RecipeRepositoryImpl"
    }

    // --- MÉTODOS QUE OBTIENEN DATOS DE LA API REMOTA ---

    /**
     * Realiza una búsqueda compleja en la API remota usando los filtros proporcionados.
     * Llama a [RecipeRemoteDataSource.complexSearchRecipes].
     * Mapea el resultado [ComplexSearchResponseDto] a una lista de [RecipeSummary].
     *
     * @return Un [Result] encapsulando [List]<[RecipeSummary]> en caso de éxito,
     * o un [Throwable] en caso de fallo de la API o del mapeo.
     */
    override suspend fun searchComplexRecipes(
        includeIngredients: List<String>?, cuisine: String?, number: Int, offset: Int
    ): Result<List<RecipeSummary>> {
        Log.d(TAG, "Repo: Iniciando búsqueda compleja (API)...")
        return remoteDataSource.complexSearchRecipes(includeIngredients, cuisine)
            .mapCatching { responseDto ->
                Log.d(TAG, "Repo: Éxito búsqueda compleja. Mapeando ${responseDto.results.size} DTOs a RecipeSummary.")
                responseDto.results.map { it.toRecipeSummary() }
            }
            .onFailure { e -> Log.e(TAG, "Repo: Error en búsqueda compleja", e) }
    }


    /**
     * Realiza una búsqueda por ingredientes en la API remota.
     * Llama a [RecipeRemoteDataSource.getRecipesByIngredients].
     * Mapea el resultado ([List]<[FindByIngredientsDto]>) a una lista de [RecipeSummary].
     *
     * @return Un [Result] encapsulando [List]<[RecipeSummary]> en caso de éxito,
     * o un [Throwable] en caso de fallo.
     */
    override suspend fun searchRecipesByIngredients(
        includeIngredients: List<String>?, ranking: Int, number: Int, offset: Int
    ): Result<List<RecipeSummary>> {
        Log.d(TAG, "Repo: Iniciando búsqueda por ingredientes (API)...")
        return remoteDataSource.getRecipesByIngredients(includeIngredients ?: emptyList(), ranking)
            .mapCatching { findByIngredientsRecipeList -> // dtoList es List<FindByIngredientsDto>
                Log.d(TAG, "Repo: Éxito búsqueda por ingredientes. Mapeando ${findByIngredientsRecipeList.size} DTOs a RecipeSummary.")
                findByIngredientsRecipeList.map { it.toRecipeSummary() }
            }
            .onFailure { e -> Log.e(TAG, "Repo: Error en búsqueda por ingredientes", e) }
    }

    /**
     * Obtiene los detalles completos de una receta desde la API remota.
     * Llama a [RecipeRemoteDataSource.fetchRecipeDetails].
     * Mapea el [RecipeDetailedDto] resultante a [RecipeDetailed] (Modelo de Dominio).
     *
     * @param id El ID de la receta.
     * @return Un [Result] encapsulando [RecipeDetailed] en caso de éxito,
     * o un [Throwable] en caso de fallo.
     */
    override suspend fun getRemoteRecipeDetails(id: Int): Result<RecipeDetailed> {
        Log.d(TAG, "Repo: Obteniendo detalles REMOTOS para ID: $id")
        return remoteDataSource.fetchRecipeDetails(id)
            .mapCatching {
                Log.d(TAG, "Repo: Éxito obteniendo detalles API. Mapeando DTO a RecipeDetailed.")
                it.toRecipeDetailed()
            }
            .onFailure { e -> Log.e(TAG, "Repo: Error obteniendo detalles REMOTOS ID: $id", e) }
    }

    // --- MÉTODOS QUE OBTIENEN DATOS DE LA BASE DE DATOS LOCAL (FAVORITOS) ---

    /**
     * Obtiene un [Flow] que emite continuamente la lista de recetas favoritas locales.
     * Llama a [RecipeLocalDataSource.getAllRecipesStream].
     * Mapea la lista de [RecipeEntity] emitida a una lista de [RecipeSummary].
     *
     * @return Un [Flow] que emite [List]<[RecipeSummary]>.
     */
    override fun getFavoriteRecipesStream(): Flow<List<RecipeSummary>> {
        Log.d(TAG, "Repo: Obteniendo stream de favoritos locales...")
        return localDataSource.getAllRecipesStream()
            .map { recipesList -> Log.d(TAG, "Repo: Stream de favoritos emitió ${recipesList.size} entidades. Mapeando a RecipeSummary.")
                recipesList.map { it.toRecipeSummary() }
            }
    }

    /**
     * Obtiene un [Flow] que emite los detalles completos de una receta favorita local.
     * Combina los Flows de la entidad de receta y los detalles de uso de ingredientes del DataSource local.
     * Mapea el resultado combinado a un [RecipeDetailed] (modelo de dominio) o `null`.
     *
     * @param id El ID de la receta favorita.
     * @return Un [Flow] que emite [RecipeDetailed?].
     */
    override fun getLocalFavoriteRecipeDetailsStream(id: Int): Flow<RecipeDetailed?> {
        Log.d(TAG, "Repo: Obteniendo stream de detalles LOCALES para ID: $id")
        val recipeEntityFlow: Flow<RecipeEntity?> = localDataSource.getRecipeByIdStream(id)
        val ingredientsPojoFlow: Flow<List<IngredientUsageDetailsPojo>> =
            localDataSource.getIngredientUsageDetailsStream(id)

        // Combina los dos Flows. La lambda se ejecuta cuando cualquiera de ellos emite un nuevo valor.
        return recipeEntityFlow.combine(ingredientsPojoFlow) { recipeEntity, ingredientsPojoList ->
            // Dentro de la lambda, tenemos el último valor emitido por cada Flow.
            // 'recipeEntity' puede ser null si la receta no existe o fue borrada.
            // Si la entidad de receta existe, la mapeamos junto con sus ingredientes POJO.
            // Usamos el mapper RecipeEntity.toRecipeDetailed
            recipeEntity?.toRecipeDetailed(ingredientPojos = ingredientsPojoList)
            // Si recipeEntity es null, el resultado de la lambda (y la emisión del Flow) es null.
        }
    }


    // --- MÉTODOS DE ESCRITURA EN LA BASE DE DATOS LOCAL (FAVORITOS) ---

    /**
     * Guarda una receta (proporcionada como [RecipeDetailed]) en la base de datos local.
     * Mapea el modelo de dominio a las entidades de Room y ejecuta las inserciones
     * necesarias (RecipeEntity, IngredientEntity, AuxRecipeIngredientEntity) dentro
     * de una transacción Room para asegurar la atomicidad (o todo o nada).
     *
     * @param recipe El [RecipeDetailed] (modelo de dominio) a guardar.
     * @return Un [Result] [Unit] indicando éxito o fallo de la operación completa.
     */

    override suspend fun addFavorite(recipe: RecipeDetailed): Result<Unit> { // Expects Result<Unit>
        Log.d(TAG, "Repo: Solicitud para agregar favorito ID: ${recipe.id}")
        // Usamos runCatching para manejar cualquier excepción durante el mapeo o la transacción BD.
        return runCatching {
            Log.d(TAG, "Repo: Mapeando RecipeDetailed a Entities para ID: ${recipe.id}")
            val recipeEntity = recipe.toRecipeEntity()
            val ingredientEntities = recipe.ingredients.map { it.toIngredientEntity() }
            val relationEntities = recipe.ingredients.map { it.toAuxRecipeIngredientEntity(recipe.id) }

            database.withTransaction { // Ejecuta todas las operaciones dentro de una transacción
                localDataSource.insertRecipe(recipeEntity)
                localDataSource.insertIngredients(ingredientEntities)
                localDataSource.insertRecipeRelations(relationEntities)
            }
        }
            .onSuccess {
                Log.i(TAG, "Repo: Receta ID ${recipe.id} guardada como favorita.")
            }.onFailure { e -> Log.e(TAG, "Repo: Error al agregar favorito ID: ${recipe.id}", e) }
    }


    /**
     * Elimina una receta favorita de la base de datos local por su ID.
     * Llama a [RecipeLocalDataSource.deleteRecipeById]. La eliminación en cascada
     * configurada en la entidad de unión debería borrar las relaciones automáticamente.
     *
     * @param id El ID de la receta a eliminar.
     * @return Un [Result] [Unit] indicando éxito o fallo.
     */
    override suspend fun removeFavorite(id: Int): Result<Unit> {
        Log.d(TAG, "Repo: Solicitud para eliminar favorito ID: $id")
        return runCatching {
            localDataSource.deleteRecipeById(id)
            Log.i(TAG, "Repo: Receta ID $id eliminada de favoritos.")
            Unit
        }.onFailure { e -> Log.e(TAG, "Repo: Error al eliminar favorito ID: $id", e) }
    }
}