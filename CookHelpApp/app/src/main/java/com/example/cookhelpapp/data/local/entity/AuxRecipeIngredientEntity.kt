package com.example.cookhelpapp.data.local.entity

import androidx.compose.ui.unit.TextUnit
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Tabla intermedia para la relación N:M entre Recetas e Ingredientes.
 * Cada fila conecta una receta (`recipeId`) con un ingrediente (`ingredientId`)
 * y almacena información específica de esa conexión.
 */
@Entity(
    tableName = "aux_recipes_ingredients",
    primaryKeys = ["recipeId", "ingredientId"],
    indices = [
        Index(value = ["recipeId"]),        // Índice para búsquedas por receta
        Index(value = ["ingredientId"])     // Índice para búsquedas por ingrediente
    ],
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AuxRecipeIngredientEntity(
    val recipeId: Int,
    val ingredientId: Int,
    val amount: Double?,
    val unit: String?
)