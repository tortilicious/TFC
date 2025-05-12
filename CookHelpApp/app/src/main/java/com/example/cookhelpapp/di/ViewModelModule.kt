package com.example.cookhelpapp.di

import com.example.cookhelpapp.presentation.viewmodel.RecipeDetailViewModel
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
     */
    viewModelOf(::RecipeSearchViewModel)

    /**
     * Proveedor para [ShowRecipesViewModel].
     * Este ViewModel gestiona el estado y la lógica para la pantalla que muestra
     * los resultados de búsqueda de recetas o la lista de favoritos.
     * Requiere [SearchComplexRecipesUseCase], [GetFavoriteRecipesStreamUseCase]
     * y [SavedStateHandle], que Koin inyectará automáticamente.
     */
    viewModel { params ->
        ShowRecipesViewModel(
            savedStateHandle = params.get(),
            searchComplexRecipesUseCase = get(),
            getFavoriteRecipesStreamUseCase = get(),
            searchRecipesByIngredientsUseCase = get()
        )
    }

    /**
     * Proveedor para [RecipeDetailViewModel].
     * Necesita los UseCases correspondientes y SavedStateHandle para el recipeId.
     */
    viewModel { params ->
        RecipeDetailViewModel(
            savedStateHandle = params.get(),
            getRecipeDetailsUseCase = get(),
            isFavoriteStreamUseCase = get(),
            addFavoriteUseCase = get(),
            removeFavoriteUseCase = get()
        )
    }
}
