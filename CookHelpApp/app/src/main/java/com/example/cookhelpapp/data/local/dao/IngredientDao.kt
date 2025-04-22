package com.example.cookhelpapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.cookhelpapp.data.local.entity.IngredientEntity


@Dao
interface IngredientDao {

    /**
     * Inserta una lista de ingredientes. Ignora conflictos para evitar duplicados.
     * Útil al guardar una receta para asegurar que los ingredientes existen.
     * @param ingredients Lista de IngredientEntity a insertar.
     * @return Lista de Longs con los IDs de los ingredientes insertados (o -1 si fueron ignorados).
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIngredients(ingredients: List<IngredientEntity>): List<Long>
}