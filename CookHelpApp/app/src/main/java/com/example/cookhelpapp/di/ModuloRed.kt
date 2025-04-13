package com.example.cookhelpapp.di

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.* // Necesitas importar Logging y sus componentes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val moduloRed = module {
    // Configuracion del singleton del cliente Ktor para las HTTP request
    single {
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
                    parameters.append("apiKey", "ab2c5bb3b05d4a5892e0cf580249921d")     // Agregamos la clave de API como parámetro en todas las llamadas
                }
            }
        }
    }
}