package com.example.cookhelpapp.presentation.composable // O donde tengas tus pantallas

// --- Imports ---
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cookhelpapp.domain.model.IngredientDetailed
import com.example.cookhelpapp.domain.model.RecipeDetailed
import com.example.cookhelpapp.presentation.viewmodel.RecipeDetailViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla Composable que muestra los detalles completos de una receta.
 * Observa el estado de [RecipeDetailViewModel] para mostrar la información
 * y permite marcar/desmarcar la receta como favorita.
 *
 * @param navController Controlador para permitir la navegación hacia atrás.
 * @param modifier Modificador Compose estándar.
 * @param viewModel Instancia de [RecipeDetailViewModel] inyectada por Koin.
 * El ViewModel obtiene el ID de la receta del SavedStateHandle.
 */
@OptIn(ExperimentalMaterial3Api::class) // Necesario para TopAppBar y Scaffold
@Composable
fun RecipeDetailScreen(
    navController: NavController, // Necesario para el botón de volver
    modifier: Modifier = Modifier,
    viewModel: RecipeDetailViewModel = koinViewModel() // Obtiene el ViewModel
) {
    // Observa el StateFlow 'uiState' del ViewModel.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Scaffold proporciona la estructura básica de Material Design.
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Muestra el título de la receta o un texto de carga/por defecto.
                    Text(
                        text = uiState.recipe?.title
                            ?: if (uiState.isLoading) "Cargando..." else "Detalle de Receta",
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                },
                actions = {
                    // Botón de acción para favorito.
                    IconButton(onClick = viewModel::toggleFavorite) { // Llama al ViewModel
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (uiState.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                            tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                }
            )
        }
    ) { paddingValues -> // Padding proporcionado por Scaffold para evitar solapamiento con TopAppBar.

        // Contenedor principal.
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplica el padding.
        ) {
            // Decide qué mostrar basado en el estado de la UI.
            when {
                // 1. Estado de Carga (inicial).
                uiState.isLoading && uiState.recipe == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                // 2. Estado de Error.
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Error al cargar: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = viewModel::retryLoadDetails) {
                            Text("Reintentar")
                        }
                    }
                }
                // 3. Estado de Éxito (receta cargada).
                uiState.recipe != null -> {
                    // Llama al Composable que renderiza el contenido.
                    RecipeDetailContent(recipe = uiState.recipe!!) // Sabemos que no es null aquí.
                }
                // 4. Estado por defecto o inesperado.
                else -> {
                    Text("No se encontraron detalles.", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

/**
 * Composable interno que muestra el contenido detallado de la receta.
 *
 * @param recipe El objeto [RecipeDetailed] a mostrar.
 * @param modifier Modificador Compose.
 */
@Composable
private fun RecipeDetailContent(recipe: RecipeDetailed, modifier: Modifier = Modifier) {
    // Columna con scroll vertical.
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp) // Padding general del contenido.
    ) {
        // Imagen
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(recipe.imageUrl).crossfade(true).build(),
            contentDescription = "Imagen de ${recipe.title}",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Título
        Text(
            recipe.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Info Rápida
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            recipe.readyInMinutes?.let {
                Text(
                    "⏱️ Tiempo: $it min",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            recipe.servings?.let {
                Text(
                    "👥 Porciones: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        // Cocinas
        recipe.cuisines?.takeIf { it.isNotEmpty() }?.let { cuisines ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Tipo de Cocina: ${cuisines.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Ingredientes
        Text("Ingredientes", style = MaterialTheme.typography.titleMedium)
        Divider(modifier = Modifier.padding(vertical = 4.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (recipe.ingredients.isNotEmpty()) {
                recipe.ingredients.forEach { IngredientItem(ingredient = it) }
            } else {
                Text(
                    "No hay información de ingredientes.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Instrucciones
        Text("Instrucciones", style = MaterialTheme.typography.titleMedium)
        Divider(modifier = Modifier.padding(vertical = 4.dp))
        if (!recipe.instructions.isNullOrBlank()) {
            Text(recipe.instructions, style = MaterialTheme.typography.bodyMedium)
        } else {
            Text("Instrucciones no disponibles.", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Composable para mostrar un item de ingrediente en la lista de detalles.
 */
@Composable
private fun IngredientItem(ingredient: IngredientDetailed, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        val amountStr = ingredient.amount?.let { amount ->
            if (amount == amount.toInt().toDouble()) amount.toInt()
                .toString() else amount.toString()
        } ?: ""
        val unitStr = ingredient.unit?.let { " $it" } ?: ""
        Text(
            text = "- ${amountStr}${unitStr} ${ingredient.name}".trim(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
