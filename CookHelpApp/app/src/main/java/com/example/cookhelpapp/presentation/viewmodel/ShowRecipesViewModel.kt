package com.example.cookhelpapp.presentation.viewmodel // O tu paquete ui.show_recipes

import androidx.lifecycle.SavedStateHandle // Para leer argumentos de navegación
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookhelpapp.domain.model.RecipeSummary
import com.example.cookhelpapp.domain.usecase.SearchComplexRecipesUseCase
import com.example.cookhelpapp.domain.usecase.SearchRecipesByIngredientsUseCase
import com.example.cookhelpapp.presentation.state.ShowRecipesUiState
import com.example.cookhelpapp.utils.PagingConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder // Para decodificar argumentos si los codificaste
import java.nio.charset.StandardCharsets

/**
 * ViewModel para la pantalla que muestra los resultados de búsqueda de recetas.
 * Recibe los parámetros de búsqueda iniciales a través de [SavedStateHandle],
 * llama al Caso de Uso apropiado y gestiona la paginación y el estado de la UI.
 *
 * @property searchComplexRecipesUseCase Caso de uso para la búsqueda compleja.
 * @property searchRecipesByIngredientsUseCase Caso de uso para la búsqueda por ingredientes.
 * @property savedStateHandle Manejador para acceder a los argumentos de navegación.
 */
class ShowRecipesViewModel(
    private val searchComplexRecipesUseCase: SearchComplexRecipesUseCase,
    private val searchRecipesByIngredientsUseCase: SearchRecipesByIngredientsUseCase,
    private val savedStateHandle: SavedStateHandle // Koin inyecta esto
) : ViewModel() {

    // Flujo de estado interno y mutable, solo accesible por el ViewModel.
    // Inicializa con el estado por defecto de ShowRecipesUiState.
    private val _uiState = MutableStateFlow(ShowRecipesUiState())

    // Flujo de estado público e inmutable, observado por la UI.
    val uiState: StateFlow<ShowRecipesUiState> = _uiState.asStateFlow()

    init {
        // Al crear el ViewModel, lee los argumentos de navegación pasados.
        val searchTypeArg: String = savedStateHandle.get<String>("searchType")
            // Si no se pasó 'searchType' o es null, usa "complex" por defecto.
            // Si no codificaste en createRoute, URLDecoder no es necesario.
            ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.name()) } ?: "complex"

        val ingredientsString: String? = savedStateHandle.get<String>("ingredients")
            // Si el argumento es la cadena literal "null", trátalo como null real.
            ?.takeIf { it != "null" }
        // Si no codificaste en createRoute, URLDecoder no es necesario.
        // ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.name()) }


        val cuisine: String? = savedStateHandle.get<String>("cuisine")
            ?.takeIf { it != "null" }
        // ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.name()) }

        val rankingString: String? = savedStateHandle.get<String>("ranking")
            ?.takeIf { it != "null" }
        val ranking: Int? = rankingString?.toIntOrNull()

        // Convierte el string de ingredientes (separado por comas) a una lista.
        val initialIngredientsList = ingredientsString
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() } // Evita strings vacíos en la lista

        // Actualiza el estado inicial con los filtros leídos.
        _uiState.update {
            it.copy(
                searchType = searchTypeArg,
                initialIngredients = initialIngredientsList,
                initialCuisine = cuisine,
                initialRanking = ranking
            )
        }

        // Realiza la primera carga de recetas con los filtros obtenidos.
        fetchRecipes(isNewSearch = true)
    }

    /**
     * Obtiene recetas (primera página o siguientes) basado en los filtros iniciales
     * guardados en el estado y el estado de paginación actual.
     * @param isNewSearch True para la primera carga (resetea lista y offset), False para cargar más.
     */
    fun fetchRecipes(isNewSearch: Boolean = true) {
        // Evita iniciar una nueva carga si ya hay una en progreso.
        if (uiState.value.isLoadingInitial || uiState.value.isLoadingMore) return

        val offsetToUse: Int // El offset que se usará para la llamada API.
        if (isNewSearch) {
            // Si es una búsqueda nueva, el offset es el inicial (0).
            offsetToUse = PagingConstants.INITIAL_RECIPE_OFFSET
            // Actualiza el estado para reflejar la carga inicial.
            _uiState.update {
                it.copy(
                    isLoadingInitial = true, // Activa indicador de carga.
                    recipes = emptyList(),   // Limpia la lista de recetas anterior.
                    currentOffset = offsetToUse, // Guarda el offset que se usará.
                    canLoadMore = false,     // Aún no sabemos si hay más páginas.
                    error = null,            // Limpia cualquier error previo.
                    noResults = false        // Resetea el flag de "sin resultados".
                )
            }
        } else {
            // Si es para cargar más, primero verifica si se permite.
            if (!uiState.value.canLoadMore) return // No hacer nada si ya no hay más.
            // Usa el offset actual guardado en el estado (que apunta a la siguiente página).
            offsetToUse = uiState.value.currentOffset
            // Actualiza el estado para reflejar que se está cargando "más".
            _uiState.update { it.copy(isLoadingMore = true, error = null, noResults = false) }
        }

        // Lanza una corutina en el scope del ViewModel (se cancela automáticamente).
        viewModelScope.launch {
            val currentUiState = uiState.value // Captura el estado actual para usar sus filtros.
            val numberPerPage = currentUiState.numberPerPage

            // Decide qué Caso de Uso llamar basado en el 'searchType' guardado.
            val result: Result<List<RecipeSummary>> = when (currentUiState.searchType) {
                "complex" -> {
                    searchComplexRecipesUseCase(
                        includeIngredients = currentUiState.initialIngredients,
                        cuisine = currentUiState.initialCuisine,
                        offset = offsetToUse,
                        number = numberPerPage
                    )
                }
                "byIngredients" -> {
                    // Para esta búsqueda, asumimos que los ingredientes y el ranking son obligatorios.
                    // La pantalla anterior debería asegurar que no son nulos.
                    // Si initialIngredients o initialRanking fueran null aquí, indicaría un error de lógica.
                    if (currentUiState.initialIngredients.isNullOrEmpty() || currentUiState.initialRanking == null) {
                        Result.failure(IllegalArgumentException("Ingredientes y ranking son necesarios para la búsqueda por ingredientes."))
                    } else {
                        searchRecipesByIngredientsUseCase(
                            includeIngredients = currentUiState.initialIngredients, // No necesita '!!' si la lógica lo asegura
                            ranking = currentUiState.initialRanking,             // No necesita '!!'
                            offset = offsetToUse,
                            number = numberPerPage
                        )
                    }
                }
                else -> {
                    // Tipo de búsqueda desconocido, actualiza el estado con un error.
                    _uiState.update {
                        it.copy(
                            isLoadingInitial = false, isLoadingMore = false,
                            error = "Tipo de búsqueda desconocido: ${currentUiState.searchType}",
                            canLoadMore = false, noResults = true
                        )
                    }
                    return@launch // Sale de la corutina.
                }
            }

            // Procesa el resultado de la llamada al Caso de Uso.
            result.onSuccess { recipeList -> // recipeList es List<RecipeSummary>
                _uiState.update { currentState ->
                    // Si es una búsqueda nueva, reemplaza la lista; si no, añade a la existente.
                    val currentRecipes = if (isNewSearch) emptyList() else currentState.recipes
                    val updatedList = currentRecipes + recipeList
                    // Calcula el offset para la *siguiente* petición.
                    val nextOffset = offsetToUse + recipeList.size
                    // Determina si se puede seguir cargando más.
                    val canStillLoadMore = recipeList.isNotEmpty() && recipeList.size >= numberPerPage

                    currentState.copy(
                        isLoadingInitial = false, // Desactiva indicadores de carga.
                        isLoadingMore = false,
                        recipes = updatedList, // Lista actualizada.
                        currentOffset = nextOffset, // Guarda el offset para la próxima.
                        canLoadMore = canStillLoadMore, // Actualiza el flag.
                        error = null, // Limpia error si hubo éxito.
                        // Actualiza noResults: si la lista final está vacía después de la primera carga
                        // o si una carga de "más" no trae nada nuevo y la lista ya estaba vacía.
                        noResults = updatedList.isEmpty() && (isNewSearch || currentState.recipes.isEmpty())
                    )
                }
            }.onFailure { exception -> // Si la llamada falló...
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoadingInitial = false, // Desactiva indicadores de carga.
                        isLoadingMore = false,
                        error = exception.localizedMessage ?: "Error desconocido", // Guarda mensaje de error.
                        // Si falla la carga inicial, no se puede cargar más.
                        // Si falla al cargar más, mantenemos el valor anterior de canLoadMore.
                        canLoadMore = if (isNewSearch) false else currentState.canLoadMore,
                        noResults = currentState.recipes.isEmpty() // Si no hay recetas y falla, es noResults.
                    )
                }
            }
        }
    }

    /**
     * Llamado por la UI para cargar la siguiente página de resultados.
     * Simplemente delega en [fetchRecipes] indicando que no es una búsqueda nueva.
     */
    fun loadMoreRecipes() {
        // Solo intenta cargar más si no está ya cargando algo y si se permite.
        if (!uiState.value.isLoadingInitial && !uiState.value.isLoadingMore && uiState.value.canLoadMore) {
            fetchRecipes(isNewSearch = false)
        }
    }
}
