package com.example.cookhelpapp.di


import androidx.room.Room
import com.example.cookhelpapp.data.local.datasource.RecipeLocalDataSource
import com.example.cookhelpapp.data.local.db.CookAppDatabase
import com.example.cookhelpapp.data.remote.api.SpoonacularApiService
import com.example.cookhelpapp.data.remote.datasource.RecipeRemoteDataSource
import com.example.cookhelpapp.data.repository.RecipeRepositoryImpl
import com.example.cookhelpapp.domain.repository.RecipeRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val dataModule = module {

    // ================================================
    // Sección: Configuración de Red (Ktor)
    // ================================================

    /**
     * Proveedor Singleton para HttpClient de Ktor.
     */
    single<HttpClient> {
        HttpClient(OkHttp) { // Configuración principal del Cliente Ktor

            // Plugin para negociación de contenido (JSON)
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true    // Ignorar claves que no recogemos de la API en JSON
                    isLenient = true            // Añade tolerancia en el JSON
                    prettyPrint = true          // Ayuda a la legibilidad del JSON
                })
            }

            // Plugin para Logging
            install(Logging) {
                logger = Logger.DEFAULT         // Usa el logger Logcat de Android por defecto
                level = LogLevel.ALL            // Mostrar todos los logs para ayudar en la depuración
            }

            //  Agregamos timeouts para las peticiones para evitar que se queden colgadas indefinidamente
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000   // Timeout total para toda la petición (30s)
                connectTimeoutMillis = 15_000   // Timeout específico para conectar (15s)
                socketTimeoutMillis = 20_000    // Timeout específico entre paquetes (lectura/escritura) (20s)
            }

            // Plugin para configurar peticiones por defecto
            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.spoonacular.com"
                    path("recipes/")
                    parameters.append("apiKey", "ab2c5bb3b05d4a5892e0cf580249921d")
                }
            }
        }
    }




    // ==================================================
    // Sección: Configuración de Base de Datos (Room)
    // ==================================================
    /**
     * Proveedor Singleton para la base de datos local.
     */
    single<CookAppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            CookAppDatabase::class.java,
            "cookhelp_recipes.db"
        ).build()
    }
    single { get<CookAppDatabase>().recipeDao() }
    single { get<CookAppDatabase>().ingredientDao() }


    // ==================================================
    // Sección: Configuración de DataSources
    // ==================================================
    /**
     * Proveedor Singleton para [RecipeLocalDataSource].
     * Koin inyecta las instancias de [RecipeDao] e [IngredientDao]
     */
    singleOf(::RecipeLocalDataSource)

    /**
     * Proveedor Singleton para la implementación concreta [RecipeRemoteDataSource].
     */
    singleOf(::RecipeRemoteDataSource)


    // ======================================================
    // Sección: Configuración del Repositorio (Capa de Datos)
    // ======================================================
    /**
     * Proveedor Singleton para la interfaz [RecipeRepository].
     * Koin creará una instancia de [RecipeRepositoryImpl] usando su constructor (singleOf)
     * y la enlazará (bind) a la interfaz [RecipeRepository].
     * Koin inyecta automáticamente las dependencias en el constructor de RecipeRepositoryImpl.
     */
    singleOf(::RecipeRepositoryImpl) bind RecipeRepository::class

}
