package com.example.cookhelpapp.presentation.viewmodel // O tu paquete de ViewModels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookhelpapp.domain.usecase.AddFavoriteUseCase
import com.example.cookhelpapp.domain.usecase.GetRemoteRecipeDetailsUseCase
import com.example.cookhelpapp.domain.usecase.IsFavoriteStreamUseCase
import com.example.cookhelpapp.domain.usecase.RemoveFavoriteUseCase
import com.example.cookhelpapp.presentation.state.RecipeDetailUiState
// Asumo que tu RecipeDetail (el que viene del UseCase) tiene una propiedad 'id'
// import com.example.cookhelpapp.domain.model.RecipeDetail // Asegúrate de importar tu modelo de dominio RecipeDetail

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Detalle de Receta.
 */
class RecipeDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getRecipeDetailsUseCase: GetRemoteRecipeDetailsUseCase,
    private val isFavoriteStreamUseCase: IsFavoriteStreamUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    companion object {
        private const val RECIPE_ID_ARG = "recipeId"
        private const val TAG = "RecipeDetailVM" // Etiqueta para los logs
    }

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    private val recipeId: Int = checkNotNull(savedStateHandle[RECIPE_ID_ARG]) {
        "El ID de la receta (recipeId) es requerido y no puede ser nulo."
    }

    init {
        Log.d(TAG, "Initializing ViewModel for recipe ID: $recipeId")
        observeFavoriteStatus()
        loadRecipeDetails()
    }

    private fun observeFavoriteStatus() {
        Log.d(TAG, "observeFavoriteStatus: Called for recipe ID: $recipeId")
        viewModelScope.launch {
            isFavoriteStreamUseCase(recipeId)
                .distinctUntilChanged()
                .collect { isFav ->
                    Log.d(TAG, "observeFavoriteStatus: Recipe ID $recipeId, isFavorite status updated to: $isFav")
                    _uiState.update { it.copy(isFavorite = isFav) }
                }
        }
    }

    private fun loadRecipeDetails() {
        Log.d(TAG, "loadRecipeDetails: CALLED for ID $recipeId. Current isLoading: ${_uiState.value.isLoading}, recipe in state: ${_uiState.value.recipe?.id == recipeId}")

        // Si ya tiene la receta correcta Y no está cargando, no hacer nada más.
        // Si está cargando, no iniciar otra carga.
        if (_uiState.value.isLoading || (_uiState.value.recipe != null && _uiState.value.recipe?.id == recipeId && !_uiState.value.isLoading)) {
            Log.d(TAG, "loadRecipeDetails: EXITING EARLY. isLoading: ${_uiState.value.isLoading}, hasCorrectRecipeAndNotLoading: ${(_uiState.value.recipe != null && _uiState.value.recipe?.id == recipeId && !_uiState.value.isLoading)}")
            // Si ya tiene la receta correcta y no estaba cargando, nos aseguramos que isLoading siga false.
            if (_uiState.value.recipe?.id == recipeId && !_uiState.value.isLoading) {
                // No es necesario actualizar si isLoading ya es false.
                // _uiState.update { it.copy(isLoading = false) } // Podría ser redundante
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, recipe = null) } // Limpia receta anterior al recargar
        Log.d(TAG, "loadRecipeDetails: Set isLoading = true. Launching coroutine for UseCase.")

        viewModelScope.launch {
            Log.d(TAG, "loadRecipeDetails: Coroutine STARTED for getRecipeDetailsUseCase ID $recipeId")
            try {
                val result = getRecipeDetailsUseCase(recipeId)
                Log.d(TAG, "loadRecipeDetails: UseCase FINISHED. Result success: ${result.isSuccess}")

                result.onSuccess { recipeDetail ->
                    // Asegúrate que recipeDetail tiene una propiedad 'id' para la comparación
                    Log.d(TAG, "loadRecipeDetails: UseCase SUCCESS. Recipe ID from detail: ${recipeDetail.id}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recipe = recipeDetail,
                            error = null
                        )
                    }
                    Log.d(TAG, "loadRecipeDetails: UI State updated on SUCCESS. isLoading is now false.")
                }.onFailure { exception ->
                    Log.e(TAG, "loadRecipeDetails: UseCase FAILURE. Exception: ${exception.message}", exception)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recipe = null,
                            error = exception.localizedMessage ?: "Error al cargar detalles de la receta"
                        )
                    }
                    Log.d(TAG, "loadRecipeDetails: UI State updated on FAILURE. isLoading is now false.")
                }
            } catch (e: Exception) {
                // Captura de seguridad para excepciones inesperadas directamente del UseCase si no usa Result
                Log.e(TAG, "loadRecipeDetails: UNCAUGHT EXCEPTION from UseCase. Exception: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipe = null,
                        error = e.localizedMessage ?: "Error inesperado al cargar detalles"
                    )
                }
            }
        }
    }

    fun toggleFavorite() {
        val currentRecipe = _uiState.value.recipe ?: run {
            Log.w(TAG, "toggleFavorite: Attempted to toggle favorite but no recipe loaded.")
            return
        }
        val isCurrentlyFavorite = _uiState.value.isFavorite
        Log.d(TAG, "toggleFavorite: Called for recipe ID ${currentRecipe.id}. Currently favorite: $isCurrentlyFavorite")

        viewModelScope.launch {
            val result = if (isCurrentlyFavorite) {
                removeFavoriteUseCase(currentRecipe.id)
            } else {
                // Asumo que addFavoriteUseCase espera el objeto RecipeDetail o similar que tienes en _uiState.value.recipe
                // y que internamente lo mapea a RecipeEntity si es necesario.
                addFavoriteUseCase(currentRecipe)
            }

            result.onSuccess {
                Log.d(TAG, "toggleFavorite: UseCase ${if(isCurrentlyFavorite) "remove" else "add"} SUCCEEDED.")
                // El estado de isFavorite se actualizará reactivamente por observeFavoriteStatus
            }.onFailure { exception ->
                Log.e(TAG, "toggleFavorite: UseCase ${if(isCurrentlyFavorite) "remove" else "add"} FAILED.", exception)
                _uiState.update {
                    it.copy(error = exception.localizedMessage ?: "Error al actualizar favorito")
                }
                // Considera limpiar el error después de un tiempo si es un mensaje temporal
            }
        }
    }

    fun retryLoadDetails() {
        Log.d(TAG, "retryLoadDetails: Called.")
        if (!_uiState.value.isLoading) {
            Log.d(TAG, "retryLoadDetails: Retrying detail load.")
            loadRecipeDetails()
        } else {
            Log.d(TAG, "retryLoadDetails: Already loading, retry skipped.")
        }
    }
}