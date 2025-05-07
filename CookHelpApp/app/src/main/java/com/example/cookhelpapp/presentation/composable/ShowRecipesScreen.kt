package com.example.cookhelpapp.presentation.composable


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cookhelpapp.domain.model.RecipeSummary
import com.example.cookhelpapp.presentation.viewmodel.ShowRecipesViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla Composable que muestra la lista de resultados de búsqueda de recetas.
 * Observa el estado de [ShowRecipesViewModel] para mostrar la lista,
 * indicadores de carga, mensajes de error y manejar la paginación ("scroll infinito").
 *
 * @param modifier Modificador Compose estándar.
 * @param viewModel Instancia de [ShowRecipesViewModel] obtenida automáticamente por Koin.
 * Los argumentos de navegación son leídos por el ViewModel a través de SavedStateHandle.
 */
@Composable
fun ShowRecipesScreen(
    modifier: Modifier = Modifier,
    viewModel: ShowRecipesViewModel = koinViewModel() // Inyecta el ViewModel
) {
    // Observa el estado de la UI desde el ViewModel de forma segura respecto al ciclo de vida
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Estado para controlar y observar el scroll de LazyColumn (para paginación)
    val listState = rememberLazyListState()

    // --- Estructura Principal de la Pantalla ---
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // Título de la pantalla (podría ser dinámico según uiState.searchType o filtros)
        Text(
            text = "Recetas Encontradas", // O un título más dinámico
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // --- Manejo de Estados de Carga y Error ---

        // 1. Carga inicial (solo se muestra si la lista está vacía y no hay error)
        if (uiState.isLoadingInitial && uiState.recipes.isEmpty() && uiState.error == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        // 2. Mensaje de error
        uiState.error?.let { errorMsg ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Error: $errorMsg",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.fetchRecipes(isNewSearch = true) }) {
                    Text("Reintentar")
                }
            }
        }

        // 3. Mensaje si no hay resultados (después de que la carga inicial termine sin error)
        if (uiState.noResults && !uiState.isLoadingInitial && uiState.error == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No se encontraron recetas para los filtros seleccionados.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // --- Lista de Resultados ---
        // Se muestra solo si no hay carga inicial O si ya hay recetas (para paginación)
        // Y si no hay error que impida mostrar la lista.
        if ((!uiState.isLoadingInitial || uiState.recipes.isNotEmpty()) && uiState.error == null && !uiState.noResults) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f), // Para que ocupe el espacio restante si hay otros elementos
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre tarjetas
            ) {
                // Renderiza cada item de receta de la lista actual en el estado
                items(
                    items = uiState.recipes,
                    key = { recipe -> recipe.id } // Key única para cada item (ayuda a Compose)
                ) { recipe ->
                    RecipeItem(recipe = recipe) // Llama al Composable que dibuja cada item
                }

                // Añade un item al final para mostrar el indicador de carga "más"
                item {
                    if (uiState.isLoadingMore) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp), // Padding para que se vea bien
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            } // Fin LazyColumn
        }
    } // Fin Column principal

    // --- Lógica Reactiva para Paginación (Scroll Infinito) ---

    // Calcula si se debería cargar más basado en el estado del scroll y el estado de la UI
    val shouldLoadMore = remember {
        derivedStateOf { // Se recalcula eficientemente solo cuando sus dependencias cambian
            val layoutInfo = listState.layoutInfo // Información sobre los items visibles
            val totalItemsInList =
                layoutInfo.totalItemsCount // Total de items actualmente en LazyColumn

            // No cargar si:
            // - La lista está vacía (la carga inicial aún no trae nada o no hay resultados).
            // - El ViewModel indica que ya no hay más (`!uiState.canLoadMore`).
            // - Ya se está cargando algo (`uiState.isLoadingMore` o `uiState.isLoadingInitial`).
            if (totalItemsInList == 0 || !uiState.canLoadMore || uiState.isLoadingMore || uiState.isLoadingInitial) {
                false
            } else {
                // Índice del último item visible en pantalla
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1

                // Umbral para disparar la carga (ej: cuando queden N items o menos por debajo para ver)
                // Ajusta el '5' según cuántos items quieres que falten para empezar a cargar.
                val loadMoreThreshold = totalItemsInList - 5

                // Cargar más si el último item visible ha superado el umbral
                lastVisibleItemIndex >= loadMoreThreshold
            }
        }
    }

    // Efecto que se dispara cuando el valor de `shouldLoadMore.value` cambia a true
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            // Llama al ViewModel para pedir la siguiente página de resultados
            viewModel.loadMoreRecipes()
        }
    }

} // Fin Composable ShowRecipesScreen

/**
 * Composable simple para mostrar la información de un [RecipeSummary] en una tarjeta.
 * Muestra la imagen y el título de la receta.
 *
 * @param recipe El resumen de la receta [RecipeSummary] a mostrar.
 * @param modifier Modificador Compose estándar.
 */
@Composable
fun RecipeItem(recipe: RecipeSummary, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Sombra ligera
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp) // Padding interno de la tarjeta
        ) {
            // Imagen de la receta usando Coil
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.imageUrl) // URL de la imagen
                    .crossfade(true) // Efecto de fundido suave
                    // .placeholder(R.drawable.placeholder_image) // Opcional: Placeholder
                    // .error(R.drawable.error_image) // Opcional: Imagen de error
                    .build(),
                contentDescription = "Imagen de ${recipe.title}",
                modifier = Modifier
                    .size(88.dp) // Tamaño de la imagen
                    .padding(end = 8.dp), // Espacio a la derecha de la imagen
                contentScale = ContentScale.Crop // Escala la imagen para llenar el espacio manteniendo aspecto
            )
            // Título de la receta
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleMedium, // Estilo de texto
                modifier = Modifier.weight(1f) // Ocupa el espacio restante en la fila
            )
        }
    }
}
