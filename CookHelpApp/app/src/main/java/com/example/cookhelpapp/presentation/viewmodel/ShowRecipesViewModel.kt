package com.example.cookhelpapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookhelpapp.domain.model.RecipeSummary // Asegúrate que la ruta es correcta
import com.example.cookhelpapp.domain.usecase.GetFavoriteRecipesStreamUseCase
import com.example.cookhelpapp.domain.usecase.SearchComplexRecipesUseCase
import com.example.cookhelpapp.navigation.NavArgs
import com.example.cookhelpapp.navigation.RecipeListMode
import com.example.cookhelpapp.presentation.state.ShowRecipesUiState // Importa tu nueva clase UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShowRecipesViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val searchComplexRecipesUseCase: SearchComplexRecipesUseCase,
    private val getFavoriteRecipesStreamUseCase: GetFavoriteRecipesStreamUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShowRecipesUiState())
    val uiState: StateFlow<ShowRecipesUiState> = _uiState.asStateFlow()

    private companion object {
        const val TAG = "ShowRecipesVM"
    }

    init {
        // Obtener argumentos de navegación y configurar el estado inicial
        val screenModeName: String? = savedStateHandle[NavArgs.SCREEN_MODE]
        val currentScreenMode: RecipeListMode = RecipeListMode.valueOf(
            screenModeName ?: RecipeListMode.ALL_RECIPES.name
        )

        val initialSearchType: String = savedStateHandle[NavArgs.SEARCH_TYPE] ?: "complex" // Se guarda en UiState pero no se usa directamente en el UseCase provisto
        val initialIngredientsString: String? = savedStateHandle[NavArgs.INGREDIENTS]
        val initialCuisineString: String? = savedStateHandle[NavArgs.CUISINE]
        val initialRankingString: String? = savedStateHandle[NavArgs.RANKING] // Se guarda en UiState pero no se usa directamente en el UseCase provisto

        _uiState.update {
            it.copy(
                searchType = initialSearchType,
                initialIngredients = initialIngredientsString?.split(',')?.map { ing -> ing.trim() }?.filter { ing -> ing.isNotEmpty() },
                initialCuisine = initialCuisineString,
                initialRanking = initialRankingString?.toIntOrNull()
            )
        }
        Log.d(TAG, "ViewModel init. Mode: $currentScreenMode, SearchType: $initialSearchType, Ingredients: $initialIngredientsString, Cuisine: $initialCuisineString, Ranking (from NavArg as String): $initialRankingString")
        loadContentBasedOnMode(currentScreenMode)
    }

    private fun loadContentBasedOnMode(mode: RecipeListMode) {
        Log.d(TAG, "loadContentBasedOnMode: $mode")
        when (mode) {
            RecipeListMode.ALL_RECIPES -> {
                fetchRemoteRecipes(
                    // Los parámetros se toman del _uiState.value donde ya están parseados y guardados
                    // searchTypeFromUiState = _uiState.value.searchType, // No es usado por el UseCase provisto
                    ingredientsListFromUiState = _uiState.value.initialIngredients, // Pasamos la List<String>?
                    cuisineFromUiState = _uiState.value.initialCuisine,
                    // rankingFromUiState = _uiState.value.initialRanking, // No es usado por el UseCase provisto
                    isInitialLoad = true
                )
            }
            RecipeListMode.FAVORITE_RECIPES -> {
                observeFavoriteRecipes()
            }
        }
    }

    fun fetchRemoteRecipes(
        // searchTypeFromUiState: String, // No es usado por el UseCase provisto
        ingredientsListFromUiState: List<String>?, // Ahora es List<String>?
        cuisineFromUiState: String?,
        // rankingFromUiState: Int?, // No es usado por el UseCase provisto
        isInitialLoad: Boolean = true
    ) {
        if (_uiState.value.isLoadingInitial || _uiState.value.isLoadingMore) {
            Log.d(TAG, "fetchRemoteRecipes: Already loading, skipping.")
            return
        }

        val currentOffsetToUse = if (isInitialLoad) 0 else _uiState.value.currentOffset
        val numberToFetch = _uiState.value.numberPerPage

        Log.d(TAG, "fetchRemoteRecipes: IngList: $ingredientsListFromUiState, Cui: $cuisineFromUiState, Offset: $currentOffsetToUse, Initial: $isInitialLoad")

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingInitial = isInitialLoad,
                    isLoadingMore = !isInitialLoad,
                    error = null,
                    noResults = false
                )
            }

            // --- ADAPTACIÓN DE PARÁMETROS PARA searchComplexRecipesUseCase ---
            // Los parámetros 'searchType' y 'ranking' de la navegación
            // no son utilizados por la firma actual de SearchComplexRecipesUseCase.
            // Si fueran necesarios para seleccionar diferentes endpoints o lógicas de ordenación
            // más complejas, el UseCase o el Repositorio tendrían que manejarlo.

            Log.d(TAG, "Calling UseCase with: includeIngredients=$ingredientsListFromUiState, cuisine=$cuisineFromUiState, offset=$currentOffsetToUse, number=$numberToFetch")

            val result = searchComplexRecipesUseCase(
                includeIngredients = ingredientsListFromUiState, // Directamente la List<String>?
                cuisine = cuisineFromUiState,                   // Directamente el String?
                offset = currentOffsetToUse,
                number = numberToFetch
            )
            // FIN DE ADAPTACIÓN DE PARÁMETROS

            result.onSuccess { newRecipes ->
                Log.d(TAG, "fetchRemoteRecipes: Success, ${newRecipes.size} recipes received.")
                _uiState.update { currentState ->
                    val allRecipes = if (isInitialLoad) newRecipes else currentState.recipes + newRecipes
                    currentState.copy(
                        isLoadingInitial = false,
                        isLoadingMore = false,
                        recipes = allRecipes,
                        currentOffset = currentOffsetToUse + newRecipes.size,
                        canLoadMore = newRecipes.size == numberToFetch,
                        noResults = isInitialLoad && newRecipes.isEmpty()
                    )
                }
            }.onFailure { exception ->
                Log.e(TAG, "fetchRemoteRecipes: Failure", exception)
                _uiState.update {
                    it.copy(
                        isLoadingInitial = false,
                        isLoadingMore = false,
                        error = exception.message ?: "Error al cargar recetas de la API",
                        noResults = isInitialLoad
                    )
                }
            }
        }
    }

    private fun observeFavoriteRecipes() {
        Log.d(TAG, "observeFavoriteRecipes: Starting to observe.")
        viewModelScope.launch {
            getFavoriteRecipesStreamUseCase()
                .onStart {
                    Log.d(TAG, "observeFavoriteRecipes: Flow onStart.")
                    _uiState.update { it.copy(isLoadingInitial = true, error = null, noResults = false) }
                }
                .catch { e ->
                    Log.e(TAG, "observeFavoriteRecipes: Flow error", e)
                    _uiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            error = e.message ?: "Error al cargar recetas favoritas",
                            noResults = true
                        )
                    }
                }
                .collect { favoriteRecipes ->
                    Log.d(TAG, "observeFavoriteRecipes: Flow collected ${favoriteRecipes.size} recipes.")
                    _uiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            recipes = favoriteRecipes,
                            error = null,
                            canLoadMore = false,
                            noResults = favoriteRecipes.isEmpty()
                        )
                    }
                }
        }
    }

    fun loadMoreRecipes() {
        val currentState = _uiState.value
        if (currentState.canLoadMore && !currentState.isLoadingInitial && !currentState.isLoadingMore &&
            RecipeListMode.valueOf(savedStateHandle[NavArgs.SCREEN_MODE] ?: RecipeListMode.ALL_RECIPES.name) == RecipeListMode.ALL_RECIPES) {
            Log.d(TAG, "loadMoreRecipes: Triggered.")
            fetchRemoteRecipes(
                // searchTypeFromUiState = currentState.searchType, // No es usado por el UseCase provisto
                ingredientsListFromUiState = currentState.initialIngredients,
                cuisineFromUiState = currentState.initialCuisine,
                // rankingFromUiState = currentState.initialRanking, // No es usado por el UseCase provisto
                isInitialLoad = false
            )
        } else {
            Log.d(TAG, "loadMoreRecipes: Cannot load more. CanLoadMore: ${currentState.canLoadMore}, IsLoading: ${currentState.isLoadingInitial || currentState.isLoadingMore}")
        }
    }

    fun retryLoad() {
        Log.d(TAG, "retryLoad: Triggered.")
        val currentScreenMode: RecipeListMode = RecipeListMode.valueOf(
            savedStateHandle[NavArgs.SCREEN_MODE] ?: RecipeListMode.ALL_RECIPES.name
        )
        _uiState.update { it.copy(currentOffset = 0, recipes = emptyList()) } // Reset para carga inicial
        loadContentBasedOnMode(currentScreenMode)
    }
}
