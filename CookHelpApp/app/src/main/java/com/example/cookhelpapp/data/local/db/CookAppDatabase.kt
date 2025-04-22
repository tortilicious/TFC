package com.example.cookhelpapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cookhelpapp.data.local.db.Converters
import com.example.cookhelpapp.data.local.dao.IngredientDao
import com.example.cookhelpapp.data.local.dao.RecipeDao
import com.example.cookhelpapp.data.local.entity.AuxRecipeIngredientEntity
import com.example.cookhelpapp.data.local.entity.IngredientEntity
import com.example.cookhelpapp.data.local.entity.RecipeEntity


/**
 * Clase abstracta principal de la base de datos Room para la aplicación CookHelpApp.
 * Hereda de RoomDatabase. Room generará la implementación concreta.
 *
 * @property entities Lista de todas las clases anotadas con @Entity que pertenecen a esta BD.
 * @property version Número de versión del esquema de la BD. Debe incrementarse si el esquema cambia.
 */

@Database(
    entities = [
        RecipeEntity::class,
        IngredientEntity::class,
        AuxRecipeIngredientEntity::class
    ],
    version = 1      // Versión actual del esquema de la BD. Incrementar cada vez que se modifique la estructura de las tablas.
)
@TypeConverters(Converters::class)
abstract class CookAppDatabase: RoomDatabase() {

    /**
     * Proporciona acceso al Data Access Object (DAO) para operaciones relacionadas con recetas.
     * Room generará la implementación de este método.
     * @return Instancia de RecipeDao.
     */
    abstract fun recipeDao(): RecipeDao

    /**
     * Proporciona acceso al Data Access Object (DAO) para operaciones relacionadas con ingredientes.
     * @return Instancia de IngredientDao.
     */
    abstract fun ingredientDao(): IngredientDao

}