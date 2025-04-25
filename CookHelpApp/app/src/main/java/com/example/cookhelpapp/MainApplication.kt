package com.example.cookhelpapp // Asegúrate que el paquete es correcto

import android.app.Application
// Importaciones de Koin
import com.example.cookhelpapp.di.dataModule // Módulo de datos
import com.example.cookhelpapp.di.domainModule // Módulo de dominio (Use Cases)
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level // Para el nivel de log de Koin

/**
 * Clase Application personalizada para la aplicación CookHelpApp.
 * Responsable de inicializar dependencias globales como Koin al inicio de la app.
 * Debe estar registrada en el AndroidManifest.xml en la etiqueta <application>
 * con el atributo android:name=".MainApplication".
 */
class MainApplication : Application() {

    /**
     * Se llama cuando la aplicación es creada por primera vez.
     * Ideal para inicializaciones globales.
     */
    override fun onCreate() {
        super.onCreate()

        // Inicializa Koin para la inyección de dependencias
        startKoin {
            androidLogger(Level.DEBUG) // Cambiar a Level.NONE para release

            // Provee el Contexto de la aplicación a Koin
            androidContext(this@MainApplication)

            // Carga todos los módulos Koin definidos. Koin buscará definiciones en todos ellos.
            modules(
                dataModule,   // Contiene HttpClient, DataSources, Database, DAOs, Repository
                domainModule  // Contiene los Use Cases
            )
        }
    }
}