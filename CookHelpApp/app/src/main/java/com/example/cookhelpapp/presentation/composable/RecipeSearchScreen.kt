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
import androidx.navigation.NavHostController // <--- Cambiado a NavHostController por consistencia
import com.example.cookhelpapp.navigation.RecipeListMode // <--- Importar RecipeListMode
import com.example.cookhelpapp.navigation.Screen
// Si decides usar la función de extensión:
// import com.example.cookhelpapp.navigation.navigateToShowRecipes
import com.example.cookhelpapp.presentation.viewmodel.RecipeSearchViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla Composable para que el usuario introduzca filtros
 * para la búsqueda de "Nuevas Recetas" (ingredientes y tipo de cocina).
 * Al pulsar "Mostrar Recetas", navega a ShowRecipesScreen pasando los filtros.
 *
 * @param navController Controlador de navegación para moverse a otras pantallas.
 * @param modifier Modificador Compose estándar.
 * @param viewModel Instancia de [RecipeSearchViewModel] obtenida automáticamente por Koin.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSearchScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: RecipeSearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var cuisineDropdownExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

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
        Text("Configura tu Búsqueda", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Input de ingredientes
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
                })
            )
            // Ejemplo de botón para añadir explícitamente (opcional)
            // IconButton(onClick = { viewModel.addIngredient() }) {
            //     Icon(Icons.Filled.Add, contentDescription = "Añadir ingrediente")
            // }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar ingredientes añadidos
        if (uiState.selectedIngredients.isNotEmpty()) {
            Text("Ingredientes: ${uiState.selectedIngredients.joinToString(", ")}")
            // Opcional: Botón para limpiar ingredientes
            Button(
                onClick = { viewModel.clearAllIngredients() }, // Asegúrate de tener este método en ViewModel
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text("Limpiar Ingredientes")
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Aumentado espacio

        // Desplegable para seleccionar cocina
        ExposedDropdownMenuBox(
            expanded = cuisineDropdownExpanded,
            onExpandedChange = { cuisineDropdownExpanded = !cuisineDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = uiState.selectedCuisine ?: "Cualquier cocina",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Cocina (Opcional)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cuisineDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor() // Necesario para que el menú se ancle correctamente
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = cuisineDropdownExpanded,
                onDismissRequest = { cuisineDropdownExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Cualquiera (Sin filtro)") },
                    onClick = {
                        viewModel.onCuisineSelected(null)
                        cuisineDropdownExpanded = false
                    }
                )
                uiState.availableCuisines.forEach { cuisine ->
                    DropdownMenuItem(
                        text = { Text(cuisine) },
                        onClick = {
                            viewModel.onCuisineSelected(cuisine)
                            cuisineDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp)) // Aumentado espacio

        // Botón para NAVEGAR a la pantalla de resultados
        Button(
            onClick = {
                focusManager.clearFocus()
                val ingredientsArg = uiState.selectedIngredients.takeIf { it.isNotEmpty() }
                    ?.joinToString(",")
                val cuisineArg = uiState.selectedCuisine
                val searchTypeArg = "complex" // Tipo de búsqueda para "New Recipes"

                // Navega usando la función createRoute de tu objeto Screen,
                // asegurándote de pasar el RecipeListMode.ALL_RECIPES.
                navController.navigate(
                    Screen.ShowRecipes.createRoute(
                        mode = RecipeListMode.ALL_RECIPES, // <--- MODO AÑADIDO
                        searchType = searchTypeArg,
                        ingredients = ingredientsArg,
                        cuisine = cuisineArg,
                        ranking = null // Ranking no se usa en esta búsqueda "complex"
                    )
                )
                // Alternativamente, usando la función de extensión (si la tienes y prefieres):
                // navController.navigateToShowRecipes(
                //     mode = RecipeListMode.ALL_RECIPES,
                //     searchType = searchTypeArg,
                //     ingredients = ingredientsArg,
                //     cuisine = cuisineArg,
                //     ranking = null
                // )
            },
            // Habilitado si hay al menos un ingrediente o una cocina seleccionada,
            // o si se permite búsqueda sin filtros (ajusta según tu lógica de negocio).
            // Para una búsqueda "complex" sin filtros, podría estar siempre habilitado
            // o podrías requerir al menos un ingrediente si la API lo necesita.
            // Por ahora, lo dejo como estaba:
            enabled = (uiState.selectedIngredients.isNotEmpty() || uiState.selectedCuisine != null),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Mostrar Recetas")
        }
    }
}
