package com.example.cookhelpapp.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.cookhelpapp.navigation.navigateToShowRecipesComplexSearch
import com.example.cookhelpapp.presentation.viewmodel.RecipeComplexSearchViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla Composable para que el usuario introduzca filtros
 * para la búsqueda de "Nuevas Recetas" (ingredientes y tipo de cocina).
 * Al pulsar "Mostrar Recetas", navega a ShowRecipesScreen en modo API_COMPLEX_SEARCH.
 *
 * @param navController Controlador de navegación para moverse a otras pantallas.
 * @param modifier Modificador Compose estándar.
 * @param viewModel Instancia de [RecipeComplexSearchViewModel] obtenida automáticamente por Koin.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSearchScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: RecipeComplexSearchViewModel = koinViewModel()
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
                focusManager.clearFocus()
            }
    ) {
        Text(
            "Configura tu Búsqueda de Nuevas Recetas",
            style = MaterialTheme.typography.headlineMedium
        )
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
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar ingredientes añadidos
        if (uiState.selectedIngredients.isNotEmpty()) {
            Text("Ingredientes: ${uiState.selectedIngredients.joinToString(", ")}")
            Button(
                onClick = { viewModel.clearAllIngredients() }, // Asegúrate de tener este método en ViewModel
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text("Limpiar Ingredientes")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para NAVEGAR a la pantalla de resultados
        Button(
            onClick = {
                focusManager.clearFocus()
                val ingredientsArg = uiState.selectedIngredients.takeIf { it.isNotEmpty() }
                    ?.joinToString(",")
                val cuisineArg = uiState.selectedCuisine

                // Navega usando la función de extensión específica para búsqueda compleja
                navController.navigateToShowRecipesComplexSearch( // <--- CAMBIO AQUÍ
                    ingredients = ingredientsArg,
                    cuisine = cuisineArg
                )
            },
            // Habilitado si se permite búsqueda sin filtros o si hay al menos un ingrediente/cocina.
            // Si tu API o lógica de búsqueda compleja requiere al menos un ingrediente,
            // podrías cambiar enabled a: uiState.selectedIngredients.isNotEmpty()
            // Por ahora, se mantiene la lógica anterior:
            enabled = (uiState.selectedIngredients.isNotEmpty() || uiState.selectedCuisine != null),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Mostrar Recetas")
        }
    }
}
