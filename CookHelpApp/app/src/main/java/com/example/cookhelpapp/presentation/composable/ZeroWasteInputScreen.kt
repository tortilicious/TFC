package com.example.cookhelpapp.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.cookhelpapp.navigation.navigateToShowRecipesByIngredientsSearch // Importa la función de extensión correcta
import com.example.cookhelpapp.presentation.viewmodel.ZeroWasteInputViewModel // Asume que crearás este ViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla Composable para que el usuario introduzca ingredientes
 * y seleccione un ranking para la búsqueda de "Aprovechamiento de Ingredientes".
 * Al pulsar "Mostrar Recetas", navega a ShowRecipesScreen en modo API_BY_INGREDIENTS_SEARCH.
 *
 * @param navController Controlador de navegación para moverse a otras pantallas.
 * @param modifier Modificador Compose estándar.
 * @param viewModel Instancia de [ZeroWasteInputViewModel] obtenida por Koin.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeroWasteInputScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ZeroWasteInputViewModel = koinViewModel() // Usa tu nuevo ViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var rankingDropdownExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Opciones para el ranking
    val rankingOptions = listOf(
        1 to "Maximizar ingredientes usados",
        2 to "Minimizar ingredientes nuevos"
    )

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .clickable( // Para quitar el foco al hacer clic fuera
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }
    ) {
        Text(
            "Aprovecha tus Ingredientes",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Input de ingredientes (igual que en RecipeSearchScreen)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = uiState.currentIngredientInput,
                onValueChange = viewModel::onIngredientInputChange,
                label = { Text("Añadir Ingrediente") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    viewModel.addIngredient()
                    // Opcional: viewModel.onIngredientInputChange("") // Limpiar input después de añadir
                })
            )
            // IconButton(onClick = { viewModel.addIngredient() }) { Icon(Icons.Default.Add, "Añadir") }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar ingredientes añadidos (igual que en RecipeSearchScreen)
        if (uiState.selectedIngredients.isNotEmpty()) {
            Text("Ingredientes: ${uiState.selectedIngredients.joinToString(", ")}")
            Button(
                onClick = { viewModel.clearAllIngredients() },
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text("Limpiar Ingredientes")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Desplegable para seleccionar el Ranking
        Text("Priorizar búsqueda por:", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 4.dp))
        ExposedDropdownMenuBox(
            expanded = rankingDropdownExpanded,
            onExpandedChange = { rankingDropdownExpanded = !rankingDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                // Muestra el texto descriptivo del ranking seleccionado
                value = rankingOptions.find { it.first == uiState.selectedRanking }?.second ?: "Seleccionar ranking",
                onValueChange = {}, // No editable directamente
                readOnly = true,
                label = { Text("Criterio de Ranking") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rankingDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor() // Necesario para que el menú se ancle correctamente
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = rankingDropdownExpanded,
                onDismissRequest = { rankingDropdownExpanded = false }
            ) {
                rankingOptions.forEach { (rankingValue, rankingText) ->
                    DropdownMenuItem(
                        text = { Text(rankingText) },
                        onClick = {
                            viewModel.onRankingSelected(rankingValue)
                            rankingDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para NAVEGAR a la pantalla de resultados
        Button(
            onClick = {
                focusManager.clearFocus()
                val ingredientsArg = uiState.selectedIngredients.joinToString(",") // Requerido
                val rankingArg = uiState.selectedRanking.toString() // Convertir Int a String para la ruta

                navController.navigateToShowRecipesByIngredientsSearch(
                    ingredients = ingredientsArg,
                    ranking = rankingArg
                )
            },
            // Habilitado solo si hay al menos un ingrediente seleccionado.
            // El ranking tiene un valor por defecto.
            enabled = uiState.selectedIngredients.isNotEmpty(),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Buscar Recetas por Ingredientes")
        }
    }
}
