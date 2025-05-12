package com.example.cookhelpapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.cookhelpapp.presentation.state.ZeroWasteInputUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para la pantalla [ZeroWasteInputScreen].
 * Gestiona el estado de los inputs del usuario: ingredientes y criterio de ranking.
 */
class ZeroWasteInputViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ZeroWasteInputUiState())
    val uiState: StateFlow<ZeroWasteInputUiState> = _uiState.asStateFlow()

    /**
     * Actualiza el texto del input del ingrediente actual a medida que el usuario escribe.
     * @param input El nuevo texto del input.
     */
    fun onIngredientInputChange(input: String) {
        _uiState.update { currentState ->
            currentState.copy(currentIngredientInput = input)
        }
    }

    /**
     * Añade el ingrediente actual (si no está vacío) a la lista de ingredientes seleccionados
     * y limpia el campo de input.
     */
    fun addIngredient() {
        val currentInput = _uiState.value.currentIngredientInput.trim()
        if (currentInput.isNotBlank()) {
            _uiState.update { currentState ->
                // Evita añadir duplicados si se desea (opcional)
                val updatedIngredients = if (currentState.selectedIngredients.contains(currentInput)) {
                    currentState.selectedIngredients
                } else {
                    currentState.selectedIngredients + currentInput
                }
                currentState.copy(
                    selectedIngredients = updatedIngredients,
                    currentIngredientInput = "" // Limpiar el campo de input
                )
            }
        }
    }

    /**
     * Elimina todos los ingredientes de la lista de ingredientes seleccionados.
     */
    fun clearAllIngredients() {
        _uiState.update { currentState ->
            currentState.copy(selectedIngredients = emptyList())
        }
    }

    /**
     * Actualiza el criterio de ranking seleccionado por el usuario.
     * @param ranking El nuevo valor del ranking (debería ser 1 o 2).
     */
    fun onRankingSelected(ranking: Int) {
        // Podrías añadir validación aquí si quieres asegurar que ranking es 1 o 2.
        _uiState.update { currentState ->
            currentState.copy(selectedRanking = ranking)
        }
    }
}
