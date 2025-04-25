package com.example.cookhelpapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.cookhelpapp.data.local.entity.AuxRecipeIngredientEntity
import com.example.cookhelpapp.data.local.entity.RecipeEntity
import com.example.cookhelpapp.data.local.pojo.IngredientUsageDetailsPojo
import kotlinx.coroutines.flow.Flow


@Dao
interface RecipeDao {


    /**
     * Agrega un nueva receta a la base de datos.
     * Si ya existe una receta con el mismo ID, se reemplazará.
     * @param recipe objeto RecipeEntity a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    /**
     * Obtiene todas las recetas guardadas (sin ingredientes).
     * Ideal para listas simples. Ordenadas por título.
     *
     * @return Flow que emite la lista de todas las RecipeEntity.
     */
    @Query("SELECT * FROM recipes ORDER BY title ASC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>


    /**
     * Obtiene una receta específica por su ID.
     * @param id ID de la receta a buscar.
     */
    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById(id: Int): Flow<RecipeEntity>


    /**
     * Elimina una receta por su ID.
     * La eliminación de las referencias asociadas en la tabla de unión
     * debería manejarse automáticamente si se configuró onDelete = CASCADE.
     * @param recipeId El ID de la receta a eliminar.
     */
    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: Int)


    /**
     * Inserta o reemplaza una lista de relaciones en la tabla de unión.
     * El Repositorio llamará a esto dentro de una transacción al guardar/actualizar.
     * @param auxEntities Lista de relaciones a insertar/reemplazar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAuxRelations(auxEntities: List<AuxRecipeIngredientEntity>)


    /**
     * Obtiene la lista detallada del uso de ingredientes para una receta específica (JOIN).
     * Alternativa al enfoque con @Relation Pojo, devuelve directamente el Pojo con los datos.
     * @param recipeId ID de la receta.
     * @return Flow con la lista de detalles de uso de ingredientes.
     */
    @Transaction
    @Query(
        """
        SELECT
            aux.recipeId, aux.ingredientId, aux.amount, aux.unit,
            i.name as ingredientName
        FROM aux_recipes_ingredients aux
        INNER JOIN ingredients i ON aux.ingredientId = i.id
        WHERE aux.recipeId = :recipeId
        ORDER BY i.name ASC
    """
    )
    fun getIngredientUsageDetailsForRecipe(recipeId: Int): Flow<List<IngredientUsageDetailsPojo>>


}