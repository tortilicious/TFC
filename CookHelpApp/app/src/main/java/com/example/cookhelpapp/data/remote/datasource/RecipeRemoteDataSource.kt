package com.example.cookhelpapp.data.remote.datasource

// Importa los DTOs y la interfaz actualizada
// Ktor y otros imports
// Log estándar de Android
// Result de Kotlin
import android.util.Log
import com.example.cookhelpapp.data.remote.api.SpoonacularApiService
import com.example.cookhelpapp.data.remote.dto.ComplexSearchResponseDto
import com.example.cookhelpapp.data.remote.dto.FindByIngredientsDto
import com.example.cookhelpapp.data.remote.dto.RecipeDetailedDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

/**
 * Implementación concreta de SpoonacularApiService.
 * Usa Ktor HttpClient para llamar a la API y android.util.Log para logging.
 * Devuelve los resultados encapsulados en Result<T>.
 *
 * @property httpClient El cliente Ktor configurado (inyectado por Koin).
 */
class RecipeRemoteDataSource(private val httpClient: HttpClient) : SpoonacularApiService {

    companion object {
        private const val TAG = "RecipeRemoteDataSource"
    }

    /**
     * Implementa la búsqueda por ingredientes.
     * La firma ahora incluye los valores por defecto de 'number' y 'offset'.
     */
    override suspend fun getRecipesByIngredients(
        includeIngredients: List<String>,
        ranking: Int,
        number: Int,
        offset: Int
    ): Result<List<FindByIngredientsDto>> {
        require(ranking == 1 || ranking == 2) { "Ranking debe ser 1 o 2. Recibido: $ranking" }
        return runCatching {
            Log.d(TAG, "Llamando a /findByIngredients")
            val response: List<FindByIngredientsDto> = httpClient.get {
                if (includeIngredients.isNotEmpty()) parameter(
                    "includeIngredients",
                    includeIngredients.joinToString(",")
                )
                url("findByIngredients")
                parameter("ingredients", includeIngredients)
                parameter("number", number)
                parameter("ranking", ranking)
            }.body()
            response
        }.onFailure { e -> Log.e(TAG, "Error en API /findByIngredients.", e) }
    }

    /**
     * Implementa la búsqueda compleja.
     * La firma incluye los valores por defecto para 'number' y 'offset'.
     */
    override suspend fun complexSearchRecipes(
        includeIngredients: List<String>?,
        cuisine: String?,
        number: Int,
        offset: Int
    ): Result<ComplexSearchResponseDto> {

        return runCatching {
            Log.d(TAG, "Llamando a /recipes/complexSearch")
            val response: ComplexSearchResponseDto = httpClient.get {
                url("complexSearch")
                if (!includeIngredients.isNullOrEmpty()) parameter("includeIngredients", includeIngredients.joinToString(","))
                if (!cuisine.isNullOrBlank()) parameter("cuisine", cuisine)
                // Parámetros de paginación (usan el valor recibido, que será el default si no se especificó otro)
                parameter("offset", offset)
                parameter("number", number)
            }.body()
            response
        }.onFailure { e -> Log.e(TAG, "Error en API /recipes/complexSearch", e) }
    }

    /**
     * Implementa la obtención de detalles de receta.
     */
    override suspend fun fetchRecipeDetails(id: Int): Result<RecipeDetailedDto> {
        return runCatching {
            Log.d(TAG, "Llamando a /recipes/$id/information")
            val response: RecipeDetailedDto = httpClient.get {
                url("$id/information")
            }.body()
            response
        }.onFailure { e -> Log.e(TAG, "Error en API /recipes/$id/information", e) }
    }
}