package com.example.cookhelpapp.data.remote.datasource

// Importa los DTOs y la interfaz
// Ktor y otros imports
// Importa la clase Log estándar de Android
// Importa Result de Kotlin
import android.util.Log
import com.example.cookhelpapp.data.remote.api.SpoonacularApiService
import com.example.cookhelpapp.data.remote.dto.ComplexSearchResponseDto
import com.example.cookhelpapp.data.remote.dto.FindByIngredientsDto
import com.example.cookhelpapp.data.remote.dto.RecetaDetalleDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

/**
 * Implementación concreta de SpoonacularApiService que utiliza Ktor HttpClient
 * para realizar las llamadas a la API remota de Spoonacular.
 * Usa android.util.Log para el registro de eventos y errores.
 * Devuelve los resultados encapsulados en Result<T>.
 *
 * @property httpClient El cliente Ktor configurado (inyectado por Koin).
 */
class RecipeRemoteDataSource(private val httpClient: HttpClient) : SpoonacularApiService {

    // Define una etiqueta TAG para los logs de esta clase
    companion object {
        private const val TAG = "RecipeRemoteDataSource"
    }

    /**
     * Busca recetas en Spoonacular basándose principalmente en una lista de ingredientes.
     * Llama al endpoint /findByIngredients.
     *
     * @param includeIngredients Lista de nombres de ingredientes.
     * @param sortBy Criterio de ordenación opcional (mapeado a 'ranking').
     * @param number Número máximo de recetas.
     * @param offset Ignorado por la API /findByIngredients.
     * @return Un [Result] que contiene una [List] de [FindByIngredientsDto] o un [Throwable].
     */
    override suspend fun buscarPorIngredientes(
        includeIngredients: List<String>,
        ranking: Int,
        number: Int,
        offset: Int
    ): Result<List<FindByIngredientsDto>> {
        val ingredientsList = includeIngredients.joinToString(",")

        return runCatching {
            // Log de Depuración: Registra qué operación se va a realizar y con qué parámetros clave.
            val logParams = "ingredients=${includeIngredients.joinToString(",")},offset=$offset, number=$number"
            Log.d(TAG, "Llamando a /recipes/findByIngredients: con $logParams")

            // Realiza la petición HTTP GET usando el cliente Ktor inyectado.
            val response: List<FindByIngredientsDto> = httpClient.get {
                // Construye la URL relativa al host base configurado en Koin (api.spoonacular.com)
                url("findByIngredients")
                // Añade los parámetros requeridos por la API a la URL final (?param1=valor1&param2=valor2...)
                parameter("ingredients", ingredientsList)
                parameter("number", number)
                parameter("ranking", ranking) // Añadimos el ranking directamente (ya validado y no nulable)
            }.body()

            // Si todo va bien (no hubo excepciones), runCatching devolverá Result.success con 'response'.
            response
        }.onFailure { e ->
            // Bloque opcional que se ejecuta SOLO si runCatching capturó una excepción 'e'.
            Log.e(TAG, "Error en API /findByIngredients. Ingredientes: $ingredientsList", e)
        }
    }

    /**
     * Realiza una búsqueda compleja de recetas con múltiples filtros opcionales.
     * Llama al endpoint /recipes/complexSearch.
     *
     * @param query Consulta de texto libre opcional.
     * @param includeIngredients Lista opcional de ingredientes.
     * @param cuisine Filtro opcional por tipo de cocina.
     * @param diet Filtro opcional por tipo de dieta.
     * @param offset Número de resultados a saltar.
     * @param number Número máximo de resultados.
     * @return Un [Result] que contiene [ComplexSearchResponseDto] o un [Throwable].
     */
    override suspend fun buscarComplejo(
        query: String?,
        includeIngredients: List<String>?,
        cuisine: String?,
        diet: String?,
        number: Int,
        offset: Int
    ): Result<ComplexSearchResponseDto> {

        return runCatching {
            // Log de depuración
            val logParams = "query=$query, ingredients=${includeIngredients?.joinToString(",")}, cuisine=$cuisine, diet=$diet, offset=$offset, number=$number"
            Log.d(TAG, "Llamando a /recipes/complexSearch con: $logParams")

            val response: ComplexSearchResponseDto = httpClient.get {
                url("/complexSearch")
                if (!query.isNullOrBlank()) parameter("query", query)
                if (!includeIngredients.isNullOrEmpty()) parameter("includeIngredients", includeIngredients.joinToString(","))
                if (!cuisine.isNullOrBlank()) parameter("cuisine", cuisine)
                if (!diet.isNullOrBlank()) parameter("diet", diet)
                parameter("offset", offset)
                parameter("number", number)
            }.body()
            response
        }.onFailure { e -> Log.e(TAG, "Error en API /recipes/complexSearch", e) }
    }

    /**
     * Obtiene la información detallada de una receta específica por su ID.
     * Llama al endpoint /recipes/{id}/information.
     *
     * @param id El ID único de la receta.
     * @return Un [Result] que contiene [RecetaDetalleDto] o un [Throwable].
     */
    override suspend fun obtenerDetallesReceta(id: Int): Result<RecetaDetalleDto> {
        return runCatching {
            // Log de depuración (o Información)
            Log.d(TAG, "Llamando a /recipes/$id/information")

            val response: RecetaDetalleDto = httpClient.get {
                url("/$id/information")
            }.body()
            response
        }.onFailure { e ->
            // Log de error, incluyendo el ID para contexto
            Log.e(TAG, "Error en API /recipes/$id/information", e)
        }
    }
}