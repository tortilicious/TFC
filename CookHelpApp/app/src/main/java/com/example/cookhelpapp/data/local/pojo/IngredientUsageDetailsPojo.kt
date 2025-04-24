package com.example.cookhelpapp.data.local.pojo

import androidx.room.ColumnInfo

/**
 * Pojo para representar un ingrediente específico DENTRO del contexto de una receta.
 * Combina la información de uso (amount, unit de AuxRecipeIngredientEntity)
 * con la información maestra (name, id de IngredientEntity).
 */
data class IngredientUsageDetailsPojo(
    val recipeId: Int,
    val ingredientId: Int,
    val amount: Double?,
    val unit: String?,
    val ingredientName: String
)
