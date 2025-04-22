package com.example.cookhelpapp.data.local.pojo
//
//import androidx.room.Embedded
//import com.example.cookhelpapp.data.local.entity.RecipeEntity
//
///**
// * Pojo para representar una Receta completa con todos sus detalles, incluyendo
// * la lista de sus ingredientes con información de uso y maestra.
// * ESTE es el objeto que devolverá el DAO y que el Repositorio mapeará.
// */
//data class RecipeWithUsageDetailsPojo(
//    @Embedded
//    val recipe: RecipeEntity, // La receta base
//
//    @Relation(
//        entity = AuxRecipeIngredientEntity::class, // La entidad que conecta
//        parentColumn = "id",         // PK de RecipeEntity
//        entityColumn = "recipeId"    // FK en AuxRecipeIngredientEntity
//    )
//    val ingredientDetails: List<IngredientUsageDetail> // La lista de detalles combinados
//)