// settings.gradle.kts (Archivo en la raíz del proyecto)

pluginManagement {
    repositories {
        // Repositorios donde Gradle busca los PLUGINS
        google()        // Necesario para plugins de Android
        mavenCentral()  // Muchos plugins y librerías están aquí
        gradlePluginPortal() // El portal oficial de plugins de Gradle (donde está KSP)
    }
}

// Este bloque es para las DEPENDENCIAS, no para los plugins
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // No necesitas gradlePluginPortal() aquí generalmente para las dependencias comunes
    }
}

rootProject.name = "CookHelpApp" // O el nombre que le hayas dado
include(":app")