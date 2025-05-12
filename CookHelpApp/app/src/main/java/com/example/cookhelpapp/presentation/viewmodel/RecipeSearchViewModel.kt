package com.example.cookhelpapp.presentation.viewmodel // O tu paquete ui.search o ui.new_recipes

import androidx.lifecycle.ViewModel
import com.example.cookhelpapp.presentation.state.RecipeSearchInputState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para la pantalla de entrada de filtros para la búsqueda de recetas.
 * (Anteriormente "Nuevas Recetas", ahora se enfoca en ser el ViewModel de búsqueda general).
 * Gestiona los inputs del usuario (ingredientes, cocina) que se usarán como criterios de búsqueda.
 * NO realiza la búsqueda en sí, solo prepara los datos que se pasarán como argumentos
 * a la pantalla que mostrará los resultados.
 */
class RecipeSearchViewModel : ViewModel() { // Nombre de la clase cambiado

    private val _uiState = MutableStateFlow(RecipeSearchInputState())
    val uiState: StateFlow<RecipeSearchInputState> = _uiState.asStateFlow()

    /**
     * Actualiza el texto en el campo de input para añadir un nuevo ingrediente.
     * Llamado por el `onValueChange` del TextField en la UI.
     * @param newValue El nuevo texto introducido.
     */
    fun onIngredientInputChange(newValue: String) {
        _uiState.update { it.copy(currentIngredientInput = newValue) }
    }

    /**
     * Añade el ingrediente del campo de input a la lista de ingredientes seleccionados.
     * Solo añade si el texto no está vacío/blanco y no es un duplicado (ignorando mayúsculas/minúsculas).
     * Limpia el campo de input después de añadir.
     */
    fun addIngredient() {
        val ingredientToAdd = _uiState.value.currentIngredientInput.trim()
        if (ingredientToAdd.isNotBlank()) {
            // Comprueba si ya existe (ignorando mayúsculas/minúsculas para evitar duplicados visuales)
            val alreadyExists = _uiState.value.selectedIngredients.any { it.equals(ingredientToAdd, ignoreCase = true) }
            if (!alreadyExists) {
                _uiState.update { currentState ->
                    currentState.copy(
                        selectedIngredients = currentState.selectedIngredients + ingredientToAdd, // Añade a la lista
                        currentIngredientInput = "" // Limpia el input
                    )
                }
            } else {
                // Si ya existe, simplemente limpia el input
                _uiState.update { it.copy(currentIngredientInput = "") }
            }
        }
    }

    /**
     * Elimina un ingrediente de la lista de ingredientes seleccionados.
     * Útil si la UI permite al usuario quitar ingredientes de la lista (ej. con Chips).
     * @param ingredient El ingrediente a eliminar.
     */
    fun removeIngredient(ingredient: String) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedIngredients = currentState.selectedIngredients - ingredient // Elimina de la lista
            )
        }
    }

    /**
     * Actualiza la cocina seleccionada en el estado.
     * Convierte null, strings vacíos o blancos a null en el estado para "sin filtro".
     * @param cuisine La cocina seleccionada desde la UI (puede ser null).
     */
    fun onCuisineSelected(cuisine: String?) {
        // Normaliza la entrada: null, "" o " " se convierten en null real en el estado.
        val cuisineToStore = cuisine?.takeIf { it.isNotBlank() }

        // Si el valor normalizado es el mismo que ya está, no hacer nada para evitar recomposiciones.
        if (_uiState.value.selectedCuisine == cuisineToStore) return

        // Actualiza el estado.
        _uiState.update {
            it.copy(selectedCuisine = cuisineToStore)
        }
    }

    /**
     * Limpia todos los ingredientes seleccionados y el campo de input de ingrediente.
     * Útil para un botón "Limpiar Ingredientes".
     */
    fun clearAllIngredients() {
        _uiState.update { it.copy(selectedIngredients = emptyList(), currentIngredientInput = "") }
    }
}
