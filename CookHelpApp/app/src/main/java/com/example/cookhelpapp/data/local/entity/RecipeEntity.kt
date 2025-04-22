package com.example.cookhelpapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una receta guardada en la tabla 'recipes' de la base de datos local.
 * Almacena la información mínima para mostrar en la sección  "Favoritos".
 * La lista de ingredientes se gestiona a traves de una tabla intermedia.
 * El ID proviene de la API y actúa como PK aquí.
 */
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    val cuisines: List<String>?,
    val instructions: String?,
    val readyInMinutes: Int?,
    val servings: Int?
)
