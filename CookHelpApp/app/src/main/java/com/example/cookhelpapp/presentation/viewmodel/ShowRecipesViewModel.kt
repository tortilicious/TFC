package com.example.cookhelpapp.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookhelpapp.domain.usecase.GetFavoriteRecipesStreamUseCase
import com.example.cookhelpapp.domain.usecase.SearchComplexRecipesUseCase
import com.example.cookhelpapp.domain.usecase.SearchRecipesByIngredientsUseCase
import com.example.cookhelpapp.navigation.NavArgs
import com.example.cookhelpapp.navigation.ScreenDisplayMode
import com.example.cookhelpapp.presentation.state.ShowRecipesUiState
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
    private val getFavoriteRecipesStreamUseCase: GetFavoriteRecipesStreamUseCase,
    private val searchRecipesByIngredientsUseCase: SearchRecipesByIngredientsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShowRecipesUiState())
    val uiState: StateFlow<ShowRecipesUiState> = _uiState.asStateFlow()

    init {
        val screenModeName: String? = savedStateHandle[NavArgs.SCREEN_MODE]
        val currentScreenDisplayMode: ScreenDisplayMode = ScreenDisplayMode.valueOf(
            screenModeName?: ScreenDisplayMode.API_COMPLEX_SEARCH.name // Un valor por defecto seguro
        )
        val initialIngredientsString: String? = savedStateHandle[NavArgs.INGREDIENTS]
        val initialCuisineString: String? = savedStateHandle[NavArgs.CUISINE]
        val initialRankingString: String? = savedStateHandle[NavArgs.RANKING]

        _uiState.update {
            it.copy(
                initialIngredients = initialIngredientsString?.split(',')?.map { ing -> ing.trim() }
                    ?.filter { ing -> ing.isNotEmpty() },
                initialCuisine = initialCuisineString,
                initialRanking = initialRankingString?.toIntOrNull()
            )
        }
        loadContentBasedOnMode(currentScreenDisplayMode)
    }

    private fun loadContentBasedOnMode(mode: ScreenDisplayMode) {
        val title = when (mode) {
            ScreenDisplayMode.LOCAL_FAVORITES -> "Mis Recetas Favoritas"
            ScreenDisplayMode.API_BY_INGREDIENTS_SEARCH -> "Recetas por Ingredientes"
            ScreenDisplayMode.API_COMPLEX_SEARCH -> "Resultados de Búsqueda"
        }
        _uiState.update { it.copy(screenTitle = title) }

        when (mode) {
            ScreenDisplayMode.API_COMPLEX_SEARCH -> {
                fetchComplexRemoteRecipes(
                    ingredientsFromUiState = _uiState.value.initialIngredients?.joinToString(","),
                    cuisineFromUiState = _uiState.value.initialCuisine,
                    isInitialLoad = true
                )
            }

            ScreenDisplayMode.API_BY_INGREDIENTS_SEARCH -> {
                val ingredientsList = _uiState.value.initialIngredients
                val ranking = _uiState.value.initialRanking // Ya es Int?
                if (!ingredientsList.isNullOrEmpty() && ranking != null) {
                    fetchRecipesByIngredients(
                        ingredientsList = ingredientsList,
                        ranking = ranking,
                        isInitialLoad = true
                    )
                } else {
                    _uiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            error = "Faltan ingredientes o ranking para este tipo de búsqueda.",
                            noResults = true
                        )
                    }
                }
            }

            ScreenDisplayMode.LOCAL_FAVORITES -> {
                observeFavoriteRecipes()
            }
        }
    }

    private fun fetchComplexRemoteRecipes(
        ingredientsFromUiState: String?,
        cuisineFromUiState: String?,
        isInitialLoad: Boolean = true
    ) {
        if (_uiState.value.isLoadingInitial || _uiState.value.isLoadingMore) return
        val currentOffsetToUse = if (isInitialLoad) 0 else _uiState.value.currentOffset
        val numberToFetch = _uiState.value.numberPerPage

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingInitial = isInitialLoad,
                    isLoadingMore = !isInitialLoad,
                    error = null,
                    noResults = false
                )
            }
            val result = searchComplexRecipesUseCase(
                includeIngredients = ingredientsFromUiState?.split(',')?.map { it.trim() }
                    ?.filter { it.isNotEmpty() },
                cuisine = cuisineFromUiState,
                offset = currentOffsetToUse,
                number = numberToFetch
            )
            result.onSuccess { newRecipes ->
                _uiState.update { currentState ->
                    val allRecipes =
                        if (isInitialLoad) newRecipes else currentState.recipes + newRecipes
                    currentState.copy(
                        isLoadingInitial = false, isLoadingMore = false, recipes = allRecipes,
                        currentOffset = currentOffsetToUse + newRecipes.size,
                        canLoadMore = newRecipes.size == numberToFetch,
                        noResults = isInitialLoad && newRecipes.isEmpty()
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoadingInitial = false,
                        isLoadingMore = false,
                        error = exception.message ?: "Error API (Complex)",
                        noResults = isInitialLoad
                    )
                }
            }
        }
    }

    private fun fetchRecipesByIngredients(
        ingredientsList: List<String>,
        ranking: Int,
        isInitialLoad: Boolean = true
    ) {
        if (_uiState.value.isLoadingInitial || _uiState.value.isLoadingMore) return
        val currentOffsetToUse = if (isInitialLoad) 0 else _uiState.value.currentOffset
        val numberToFetch = _uiState.value.numberPerPage

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingInitial = isInitialLoad,
                    isLoadingMore = !isInitialLoad,
                    error = null,
                    noResults = false
                )
            }
            val result = searchRecipesByIngredientsUseCase(
                includeIngredients = ingredientsList,
                ranking = ranking,
                offset = currentOffsetToUse,
                number = numberToFetch
            )
            result.onSuccess { newRecipes ->
                _uiState.update { currentState ->
                    val allRecipes =
                        if (isInitialLoad) newRecipes else currentState.recipes + newRecipes
                    currentState.copy(
                        isLoadingInitial = false, isLoadingMore = false, recipes = allRecipes,
                        currentOffset = currentOffsetToUse + newRecipes.size,
                        canLoadMore = newRecipes.size == numberToFetch,
                        noResults = isInitialLoad && newRecipes.isEmpty()
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoadingInitial = false,
                        isLoadingMore = false,
                        error = exception.message ?: "Error API (ByIngredients)",
                        noResults = isInitialLoad
                    )
                }
            }
        }
    }

    private fun observeFavoriteRecipes() {
        viewModelScope.launch {
            getFavoriteRecipesStreamUseCase()
                .onStart {
                    _uiState.update {
                        it.copy(
                            isLoadingInitial = true,
                            error = null,
                            noResults = false
                        )
                    }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            error = e.message ?: "Error al cargar recetas favoritas",
                            noResults = true
                        )
                    }
                }
                .collect { favoriteRecipes ->
                    _uiState.update {
                        it.copy(
                            isLoadingInitial = false, recipes = favoriteRecipes, error = null,
                            canLoadMore = false, noResults = favoriteRecipes.isEmpty()
                        )
                    }
                }
        }
    }

    fun loadMoreRecipes() {
        val currentState = _uiState.value
        val currentScreenDisplayMode: ScreenDisplayMode = ScreenDisplayMode.valueOf(
            savedStateHandle[NavArgs.SCREEN_MODE] ?: ScreenDisplayMode.API_COMPLEX_SEARCH.name
        )

        if (currentState.canLoadMore && !currentState.isLoadingInitial && !currentState.isLoadingMore) {
            when (currentScreenDisplayMode) {
                ScreenDisplayMode.API_COMPLEX_SEARCH -> {
                    fetchComplexRemoteRecipes(
                        ingredientsFromUiState = currentState.initialIngredients?.joinToString(","),
                        cuisineFromUiState = currentState.initialCuisine,
                        isInitialLoad = false
                    )
                }

                ScreenDisplayMode.API_BY_INGREDIENTS_SEARCH -> {
                    val ingredientsList = currentState.initialIngredients
                    val ranking = currentState.initialRanking
                    if (!ingredientsList.isNullOrEmpty() && ranking != null) {
                        fetchRecipesByIngredients(
                            ingredientsList = ingredientsList,
                            ranking = ranking,
                            isInitialLoad = false
                        )
                    } else {
                        _uiState.update { it.copy(canLoadMore = false) }
                    }
                }

                ScreenDisplayMode.LOCAL_FAVORITES -> {
                }
            }
        }
    }

    fun retryLoad() {
        val currentScreenDisplayMode: ScreenDisplayMode = ScreenDisplayMode.valueOf(
            savedStateHandle[NavArgs.SCREEN_MODE] ?: ScreenDisplayMode.API_COMPLEX_SEARCH.name
        )
        _uiState.update { it.copy(currentOffset = 0, recipes = emptyList()) }
        loadContentBasedOnMode(currentScreenDisplayMode)
    }
}
