package com.example.cookhelpapp.di

import com.example.cookhelpapp.presentation.viewmodel.RecipeSearchViewModel
import com.example.cookhelpapp.presentation.viewmodel.ShowRecipesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Módulo Koin que define las dependencias para la capa de Presentación (ViewModels).
 * Especifica cómo Koin debe crear instancias de los ViewModels de la aplicación.
 */
val viewModelModule = module {

    /**
     * Proveedor para [RecipeSearchViewModel].
     * Este ViewModel gestiona el estado de los inputs en la pantalla de búsqueda de recetas.
     * Usa 'viewModelOf' ya que su constructor es simple (o no tiene argumentos que Koin deba resolver).
     * Si [RecipeSearchViewModel] no tiene argumentos en su constructor, viewModelOf(::RecipeSearchViewModel)
     * es equivalente a viewModel { RecipeSearchViewModel() }.
     */
    viewModelOf(::RecipeSearchViewModel)
    // Alternativa si RecipeSearchViewModel no tuviera constructor con argumentos inyectables:
    // factoryOf(::RecipeSearchViewModel) // Si no necesitara ser un ViewModel de Jetpack (raro)
    // viewModel { RecipeSearchViewModel() } // Si es un ViewModel y no tiene dependencias Koin

    /**
     * Proveedor para [ShowRecipesViewModel].
     * Este ViewModel gestiona el estado y la lógica para la pantalla que muestra
     * los resultados de búsqueda de recetas.
     * Requiere [SearchComplexRecipesUseCase], [SearchRecipesByIngredientsUseCase]
     * y [SavedStateHandle], que Koin inyectará automáticamente.
     * Se usa la lambda 'viewModel { params -> ... }' para permitir que Koin
     * proporcione el [SavedStateHandle] a través de 'params.get()'.
     */
    viewModel { params -> // 'params' permite a Koin inyectar SavedStateHandle y otros desde la Activity/Fragment
        ShowRecipesViewModel(
            searchComplexRecipesUseCase = get(), // Koin busca y provee SearchComplexRecipesUseCase
            searchRecipesByIngredientsUseCase = get(), // Koin busca y provee SearchRecipesByIngredientsUseCase
            savedStateHandle = params.get() // Koin provee el SavedStateHandle para este ViewModel
        )
    }

    // Aquí añadirías las definiciones para otros ViewModels que crees en el futuro:
    // Ejemplo:
    // viewModelOf(::FavoritesViewModel) // Si FavoritesViewModel tiene constructor simple
    // viewModel { params -> RecipeDetailViewModel(get(), params.get()) } // Si RecipeDetailViewModel necesita UseCase y SavedStateHandle
}
