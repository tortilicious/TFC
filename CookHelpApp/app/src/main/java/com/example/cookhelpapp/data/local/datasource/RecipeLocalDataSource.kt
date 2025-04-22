package com.example.cookhelpapp.data.local.datasource

import android.util.Log
import com.example.cookhelpapp.data.local.dao.IngredientDao
import com.example.cookhelpapp.data.local.dao.RecipeDao
import com.example.cookhelpapp.data.local.entity.AuxRecipeIngredientEntity
import com.example.cookhelpapp.data.local.entity.IngredientEntity
import com.example.cookhelpapp.data.local.entity.RecipeEntity
import com.example.cookhelpapp.data.local.pojo.IngredientUsageDetailsPojo
import kotlinx.coroutines.flow.Flow

/**
 * Implementación del DataSource Local.
 * Actúa como intermediario entre el Repositorio y los DAOs de Room.
 * Encapsula todas las interacciones con la base de datos local.
 *
 * @property recipeDao DAO para operaciones relacionadas con la entidad RecipeEntity y la tabla de unión.
 * @property ingredientDao DAO para operaciones relacionadas con la entidad IngredientEntity.
 */
class RecipeLocalDataSource(
    private val recipeDao: RecipeDao, // Inyectado por Koin
    private val ingredientDao: IngredientDao // Inyectado por Koin
) {

    // Definimos el TAG para los logs de esta clase
    companion object {
        private const val TAG = "RecipeLocalDataSource"
    }

    // --- OPERACIONES DE LECTURA (devuelven Flow para ser observables) ---

    /**
     * Obtiene un Flow que emite la lista completa de recetas (solo RecipeEntity)
     * almacenadas localmente, ordenadas por título.
     * El Flow se actualizará automáticamente si cambia la tabla 'recipes'.
     * @return Un Flow que emite List<RecipeEntity>.
     */
    fun getAllRecipesStream(): Flow<List<RecipeEntity>> {
        Log.d(TAG, "Obteniendo stream de todas las recetas locales")
        return recipeDao.getAllRecipes()
    }

    /**
     * Obtiene un Flow que emite una receta específica por su ID (o null si no existe).
     * El Flow se actualizará si la receta específica cambia.
     * @param recipeId El ID de la receta a observar.
     * @return Un Flow que emite RecipeEntity?
     */
    fun getRecipeByIdStream(recipeId: Int): Flow<RecipeEntity?> {
        Log.d(TAG, "Obteniendo stream para receta local ID: $recipeId")
        return recipeDao.getRecipeById(recipeId)
    }

    /**
     * Obtiene un Flow con la lista detallada del uso de ingredientes para una receta específica.
     * Utiliza la consulta JOIN definida en RecipeDao que devuelve el POJO.
     * El Flow se actualizará si cambian los datos relacionados.
     * @param recipeId El ID de la receta.
     * @return Un Flow que emite List<IngredientUsageDetailsPojo>.
     */
    fun getIngredientUsageDetailsStream(recipeId: Int): Flow<List<IngredientUsageDetailsPojo>> {
        Log.d(TAG, "Obteniendo stream de detalles de ingredientes para receta local ID: $recipeId")
        return recipeDao.getIngredientUsageDetailsForRecipe(recipeId)
    }

    // --- OPERACIONES DE ESCRITURA ---

    /**
     * Inserta o reemplaza una única receta en la base de datos.
     * Debe ser llamada desde una Coroutine.
     * @param recipe La entidad RecipeEntity a guardar.
     */
    suspend fun insertRecipe(recipe: RecipeEntity) {
        Log.d(TAG, "Insertando/reemplazando receta local con ID: ${recipe.id}")
        recipeDao.insertRecipe(recipe)
    }

    /**
     * Inserta una lista de ingredientes maestros. Ignora los que ya existen.
     * Útil para asegurar que los ingredientes de una receta están en la tabla maestra.
     * Debe ser llamada desde una Coroutine.
     * @param ingredients Lista de IngredientEntity a insertar.
     */
    suspend fun insertIngredients(ingredients: List<IngredientEntity>) {
        Log.d(
            TAG,
            "Intentando insertar ${ingredients.size} ingredientes maestros (ignorando duplicados)"
        )
        ingredientDao.insertAllIngredients(ingredients)
    }

    /**
     * Inserta o reemplaza las relaciones entre una receta y sus ingredientes en la tabla de unión.
     * Debe ser llamada desde una Coroutine.
     * @param relations Lista de AuxRecipeIngredientEntity a guardar.
     */
    suspend fun insertRecipeRelations(relations: List<AuxRecipeIngredientEntity>) {
        if (relations.isNotEmpty()) {
            Log.d(
                TAG,
                "Insertando/reemplazando ${relations.size} relaciones para receta ID: ${relations.first().recipeId}"
            )
            recipeDao.insertAllAuxRelations(relations)
        } else {
            Log.d(TAG, "No hay relaciones para insertar.")
        }
    }

    /**
     * Elimina una receta específica de la base de datos por su ID.
     * Gracias a 'onDelete = CASCADE' en la ForeignKey, las relaciones en la tabla
     * de unión también deberían eliminarse automáticamente.
     * Debe ser llamada desde una Coroutine.
     * @param recipeId El ID de la receta a eliminar.
     */
    suspend fun deleteRecipeById(recipeId: Int) {
        Log.d(TAG, "Eliminando receta local ID: $recipeId")
        recipeDao.deleteRecipeById(recipeId)
    }

}