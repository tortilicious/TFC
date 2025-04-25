package com.example.cookhelpapp.data.mapper

import com.example.cookhelpapp.data.local.entity.AuxRecipeIngredientEntity
import com.example.cookhelpapp.data.local.entity.IngredientEntity
import com.example.cookhelpapp.data.local.entity.RecipeEntity
import com.example.cookhelpapp.data.local.pojo.IngredientUsageDetailsPojo
import com.example.cookhelpapp.data.remote.dto.ComplexSearchItemDto
import com.example.cookhelpapp.data.remote.dto.FindByIngredientsDto
import com.example.cookhelpapp.data.remote.dto.IngredientInfoDto
import com.example.cookhelpapp.data.remote.dto.RecipeDetailedDto
import com.example.cookhelpapp.domain.model.IngredientDetailed
import com.example.cookhelpapp.domain.model.RecipeDetailed
import com.example.cookhelpapp.domain.model.RecipeSummary

// --- Archivo de Mappers ---

/**
 * Este archivo contiene funciones de extensión para mapear (convertir) objetos
 * entre las diferentes capas de la aplicación:
 * - DTO (Data Transfer Object): Representan la respuesta de la API remota.
 * - Entity (Entidad de Room): Representan las tablas de la base de datos local.
 * - Domain Model (Modelo de Dominio): Representan los objetos de negocio limpios
 * que usa la lógica de la aplicación y la UI.
 */


// ======================================
//  Mapeadores hacia RecipeSummary (Dominio)
// ======================================

/**
 * Convierte un [ComplexSearchItemDto] (resultado de API /complexSearch)
 * en un [RecipeSummary] (modelo de dominio para listas).
 * @receiver El DTO del item de búsqueda compleja.
 * @return El modelo de dominio [RecipeSummary] correspondiente.
 */
fun ComplexSearchItemDto.toRecipeSummary(): RecipeSummary {
    return RecipeSummary(
        id = this.id,
        title = this.title,
        imageUrl = this.image
    )
}

/**
 * Convierte un [FindByIngredientsDto] (resultado de API /findByIngredients)
 * en un [RecipeSummary] (modelo de dominio para listas).
 * @receiver El DTO del item encontrado por ingredientes.
 * @return El modelo de dominio [RecipeSummary] correspondiente.
 */
fun FindByIngredientsDto.toRecipeSummary(): RecipeSummary {
    return RecipeSummary(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl
    )
}

/**
 * Convierte una [RecipeEntity] (de la BD local Room)
 * en un [RecipeSummary].
 * @receiver La entidad de receta de la base de datos.
 * @return El modelo de dominio [RecipeSummary] correspondiente.
 */
fun RecipeEntity.toRecipeSummary(): RecipeSummary {
    return RecipeSummary(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl
    )
}


// =======================================
//  Mapeadores hacia RecipeDetailed (Dominio)
// =======================================

/**
 * Convierte un [RecipeDetailedDto] (respuesta detallada de la API /information)
 * en un [RecipeDetailed] (modelo de dominio detallado).
 * @receiver El DTO de detalle de la API.
 * @return El modelo de dominio [RecipeDetailed] correspondiente.
 */
fun RecipeDetailedDto.toRecipeDetailed(): RecipeDetailed {
    return RecipeDetailed(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl, // DTO usa 'image'
        cuisines = this.cuisines,
        instructions = this.instructions,
        readyInMinutes = this.readyInMinutes,
        servings = this.servings,
        // Mapea la lista de DTOs de ingredientes a la lista de dominio usando otro mapper.
        ingredients = this.ingredients.map { it.toIngredientDetailed() }
    )
}

/**
 * Convierte una [RecipeEntity] (de la BD local) en un [RecipeDetailed] (modelo de dominio detallado).
 * Requiere que se le pase la lista de detalles de ingredientes obtenida por separado de la BD local.
 * @receiver La entidad de receta de la base de datos.
 * @param ingredientPojos La lista de [IngredientUsageDetailsPojo] obtenida de la consulta JOIN en Room.
 * @return El modelo de dominio [RecipeDetailed] completo.
 */
fun RecipeEntity.toRecipeDetailed(ingredientPojos: List<IngredientUsageDetailsPojo>): RecipeDetailed {
    return RecipeDetailed(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        cuisines = this.cuisines,
        instructions = this.instructions,
        readyInMinutes = this.readyInMinutes,
        servings = this.servings,
        // Mapea la lista de POJOs (pasada como parámetro) a IngredienteDetalle usando otro mapper.
        ingredients = ingredientPojos.map { it.toIngredientDetailed() }
    )
}

// ==========================================
//  Mapeadores hacia IngredientDetailed (Dominio)
// ==========================================

/**
 * Convierte un [IngredientUsageDetailsPojo] (resultado de la consulta JOIN en Room)
 * en un [IngredientDetailed] (modelo de dominio).
 * @receiver El POJO con detalles de uso del ingrediente desde la BD.
 * @return El modelo de dominio [IngredientDetailed] correspondiente.
 */
fun IngredientUsageDetailsPojo.toIngredientDetailed(): IngredientDetailed {
    return IngredientDetailed(
        id = this.ingredientId,
        name = this.ingredientName,
        amount = this.amount,
        unit = this.unit,
    )
}

/**
 * Convierte un [IngredientInfoDto] (DTO simple usado en API y detalle)
 * en un [IngredientDetailed] (modelo de dominio).
 * @receiver El DTO simple del ingrediente.
 * @return El modelo de dominio [IngredientDetailed] correspondiente.
 */
fun IngredientInfoDto.toIngredientDetailed(): IngredientDetailed {
    return IngredientDetailed(
        id = this.id, // Recuerda que este ID puede ser null
        name = this.name,
        amount = this.amount,
        unit = this.unit,
    )
}


// =============================================
//  Mapeadores INVERSOS (Dominio -> Entidades BD)
// =============================================
// Necesarios para guardar favoritos en Room.

/**
 * Convierte un [RecipeDetailed] (modelo de dominio) a [RecipeEntity] (entidad de Room)
 * para ser guardada en la base de datos. Omite la lista de ingredientes, que se guarda por separado.
 * @receiver El modelo de dominio detallado de la receta.
 * @return La entidad [RecipeEntity] lista para Room.
 */
fun RecipeDetailed.toRecipeEntity(): RecipeEntity {
    return RecipeEntity(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        cuisines = this.cuisines,
        instructions = this.instructions,
        readyInMinutes = this.readyInMinutes,
        servings = this.servings
    )
}

/**
 * Convierte un [IngredientDetailed] (modelo de dominio) a [AuxRecipeIngredientEntity] (entidad de unión Room)
 * para guardar la relación entre una receta y un ingrediente, incluyendo cantidad/unidad.
 * Requiere el ID de la receta a la que pertenece.
 * @receiver El modelo de dominio detallado del ingrediente.
 * @param recipeId El ID de la receta a la que se asocia este ingrediente.
 * @return La entidad [AuxRecipeIngredientEntity] lista para Room, o null si el ID del ingrediente es nulo.
 */
fun IngredientDetailed.toAuxRecipeIngredientEntity(recipeId: Int): AuxRecipeIngredientEntity {
    return AuxRecipeIngredientEntity(
        recipeId = recipeId,
        ingredientId = this.id,
        amount = this.amount,
        unit = this.unit
    )
}

/**
 * Convierte un [IngredientDetailed] (modelo de dominio) a [IngredientEntity] (entidad maestra de Room)
 * para asegurar que el ingrediente existe en la tabla maestra de ingredientes.
 * @receiver El modelo de dominio detallado del ingrediente.
 * @return La entidad [IngredientEntity] lista para Room, o null si el ID del ingrediente es nulo.
 */
fun IngredientDetailed.toIngredientEntity(): IngredientEntity {
    return IngredientEntity(
        id = this.id,
        name = this.name
    )
}

