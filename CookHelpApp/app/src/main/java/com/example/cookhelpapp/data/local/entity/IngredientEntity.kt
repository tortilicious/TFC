package com.example.cookhelpapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Representa un ingrediente maestro en la tabla 'ingredients'.
 * Almacena información única sobre cada tipo de ingrediente encontrado.
 * El ID proviene de la API y actúa como PK aquí.
 */
@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String
)
