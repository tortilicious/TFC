package com.example.cookhelpapp.di

// Importa todos tus Casos de Uso
// Importa la función factoryOf de Koin
import com.example.cookhelpapp.domain.usecase.AddFavoriteUseCase
import com.example.cookhelpapp.domain.usecase.GetFavoriteRecipesStreamUseCase
import com.example.cookhelpapp.domain.usecase.GetLocalFavoriteRecipeDetailsStreamUseCase
import com.example.cookhelpapp.domain.usecase.GetRemoteRecipeDetailsUseCase
import com.example.cookhelpapp.domain.usecase.RemoveFavoriteUseCase
import com.example.cookhelpapp.domain.usecase.SearchComplexRecipesUseCase
import com.example.cookhelpapp.domain.usecase.SearchRecipesByIngredientsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/**
 * Módulo Koin que define las dependencias para la capa de Dominio.
 * Principalmente, define cómo crear instancias de los Casos de Uso (Use Cases).
 * Usa 'factoryOf' para crear una nueva instancia del UseCase cada vez que se solicita,
 * Koin inyectará automáticamente 'RecipeRepository' (definido en dataModule) en sus constructores.
 */
val domainModule = module {

    // Define un factory para cada Caso de Uso usando la referencia a su constructor (::)
    factoryOf(::SearchComplexRecipesUseCase)
    factoryOf(::SearchRecipesByIngredientsUseCase)
    factoryOf(::GetRemoteRecipeDetailsUseCase)
    factoryOf(::GetFavoriteRecipesStreamUseCase)
    factoryOf(::AddFavoriteUseCase)
    factoryOf(::RemoveFavoriteUseCase)
    factoryOf(::GetLocalFavoriteRecipeDetailsStreamUseCase)

}