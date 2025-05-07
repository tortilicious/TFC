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
import androidx.navigation.NavController
import com.example.cookhelpapp.navigation.Screen // Asegúrate de importar tu clase Screen
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
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: RecipeSearchViewModel = koinViewModel() // O el ViewModel correcto para esta pantalla
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var cuisineDropdownExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus() // Limpia el foco al hacer clic fuera del TextField
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
                    // No quitamos foco aquí para permitir añadir varios ingredientes seguidos
                })
            )
            // Si quieres un botón "+" para añadir, iría aquí:
            // IconButton(onClick = { viewModel.addIngredient() }) { Icon(Icons.Filled.Add, "Añadir") }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar ingredientes añadidos (si hay)
        if (uiState.selectedIngredients.isNotEmpty()) {
            Text("Ingredientes: ${uiState.selectedIngredients.joinToString(", ")}")
            // Opcional: Botón para limpiar ingredientes
            Button(
                onClick = { /* viewModel.clearAllIngredients() - necesitarías este método en ViewModel */ },
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text("Limpiar Ingredientes")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Desplegable para seleccionar cocina
        ExposedDropdownMenuBox(
            expanded = cuisineDropdownExpanded,
            onExpandedChange = { cuisineDropdownExpanded = !cuisineDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = uiState.selectedCuisine ?: "Cualquier cocina",
                onValueChange = {}, // No editable directamente
                readOnly = true,
                label = { Text("Tipo de Cocina (Opcional)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cuisineDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
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

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para NAVEGAR a la pantalla de resultados
        Button(
            onClick = {
                focusManager.clearFocus() // Quita foco antes de navegar

                // Prepara los argumentos SIN URLEncoder
                val ingredientsArg = uiState.selectedIngredients.takeIf { it.isNotEmpty() }
                    ?.joinToString(",") // Si no es null y no está vacío, une con comas
                // Si es null o vacío, el resultado de takeIf es null,
                // entonces ingredientsArg será null.

                val cuisineArg = uiState.selectedCuisine // Ya es String?

                // Navega usando la función createRoute de tu objeto Screen
                navController.navigate(
                    Screen.ShowRecipes.createRoute(
                        searchType = "complex", // O el tipo de búsqueda que corresponda
                        ingredients = ingredientsArg, // Pasa el String o null
                        cuisine = cuisineArg // Pasa el String o null
                        // ranking no aplica para complexSearch
                    )
                )
            },
            // Habilitado si hay al menos un ingrediente o una cocina seleccionada,
            enabled = (uiState.selectedIngredients.isNotEmpty() || uiState.selectedCuisine != null),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Mostrar Recetas")
        }
    }
}
