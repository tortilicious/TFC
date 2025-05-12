package com.example.cookhelpapp.presentation.composable

// Quita los imports de Scaffold, TopAppBar, TopAppBarDefaults, Icons, IconButton si ya no se usan en otras partes del archivo.
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.automirrored.filled.ArrowBack
// import androidx.compose.material3.Icon
// import androidx.compose.material3.IconButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cookhelpapp.domain.model.RecipeSummary
import com.example.cookhelpapp.navigation.Screen
import com.example.cookhelpapp.presentation.viewmodel.ShowRecipesViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla que muestra la lista de resultados de búsqueda de recetas o favoritos.
 * Permite hacer clic en un item para navegar a la pantalla de detalle.
 * El título de la pantalla es dinámico y se muestra en la parte superior.
 *
 * @param navController Controlador de navegación para ir a otras pantallas.
 * @param modifier Modificador Compose estándar.
 * @param viewModel Instancia de [ShowRecipesViewModel] obtenida por Koin.
 */
@Composable
fun ShowRecipesScreen(
    modifier: Modifier = Modifier,
    viewModel: ShowRecipesViewModel = koinViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // --- Estructura Principal de la Pantalla ---
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título dinámico de la pantalla
        Text(
            text = uiState.screenTitle,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // Muestra un indicador de carga si isLoadingInitial es true
        if (uiState.isLoadingInitial) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        // Mensaje de error
        else if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Error: ${uiState.error}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        // Mensaje si no hay resultados
        else if (uiState.noResults) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No se encontraron recetas.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // --- Lista de Resultados ---
        else if (uiState.recipes.isNotEmpty() || !uiState.isLoadingInitial) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.recipes,
                    key = { recipe -> recipe.id }
                ) { recipe ->
                    RecipeItem(
                        recipe = recipe,
                        onItemClick = { recipeId ->
                            navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                        }
                    )
                }

                item {
                    if (uiState.isLoadingMore) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp), // Padding vertical para el indicador
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    // --- Lógica Reactiva para Paginación (Scroll Infinito) ---
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsInList = layoutInfo.totalItemsCount
            if (totalItemsInList == 0 || !uiState.canLoadMore || uiState.isLoadingMore || uiState.isLoadingInitial) {
                false
            } else {
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                val loadMoreThreshold = totalItemsInList - 5
                lastVisibleItemIndex >= loadMoreThreshold
            }
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadMoreRecipes()
        }
    }
}

/**
 * Composable simple para mostrar la información de un [RecipeSummary] en una tarjeta.
 * (Se mantiene igual que tu versión anterior)
 */
@Composable
fun RecipeItem(recipe: RecipeSummary, modifier: Modifier = Modifier, onItemClick: (Int) -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(recipe.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen de ${recipe.title}",
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
