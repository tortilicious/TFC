plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization) // Para Ktor JSON
    alias(libs.plugins.ksp) // Para Room
    alias(libs.plugins.kotlin.compose.compiler)
}

android {
    namespace = "com.example.cookhelpapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cookhelpapp"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core AndroidX y Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose (BOM gestiona las versiones de las siguientes librerías de Compose)
    implementation(platform(libs.androidx.compose.bom)) // Importante: Plataforma BOM
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview) // Solo para Previews en el IDE
    debugImplementation(libs.androidx.compose.ui.tooling) // Herramientas de Compose (Layout Inspector) solo en debug

    // Lifecycle Compose Extensions (ViewModel y collectAsStateWithLifecycle)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Extensiones Kotlin para Room

    ksp(libs.androidx.room.compiler) // Usa KSP para el compilador de Room

    // Ktor (Cliente HTTP)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp) // Engine
    implementation(libs.ktor.client.content.negotiation) // Para JSON/XML etc.
    implementation(libs.ktor.serialization.kotlinx.json) // Serializador JSON
    implementation(libs.kotlinx.serialization.json)      // Dependencia de kotlinx-serialization
    implementation(libs.ktor.client.logging) // Opcional: para logs de Ktor

    // Koin (Inyección de Dependencias)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Coil (Carga de Imágenes en Compose)
    implementation(libs.coil.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}
