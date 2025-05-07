// presentation/composable/MainMenuScreen.kt (o donde tengas tus pantallas)
package com.example.cookhelpapp.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController // Importar NavController
import com.example.cookhelpapp.navigation.Screen // Importar tus rutas

/**
 * Pantalla principal de la aplicación que muestra las opciones del menú.
 *
 * @param navController Controlador de navegación para moverse a otras pantallas.
 */
@Composable
fun MainMenuScreen(
    navController: NavController, // Recibe el NavController
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("CookHelp App", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        // Botón 1: Nuevas Recetas (lleva a RecipeSearchScreen)
        Button(
            onClick = { navController.navigate(Screen.NewRecipesInput.route) },
            modifier = Modifier.width(240.dp).padding(vertical = 8.dp)
        ) {
            Text("New Recipes")
        }

        // Botón 2: Aprovechamiento
        Button(
            onClick = { navController.navigate(Screen.ZeroWasteRecipesInput.route) },
            modifier = Modifier.width(240.dp).padding(vertical = 8.dp)
        ) {
            Text("Zero-Waste Recipes")
        }

        // Botón 3: Favoritos
        Button(
            onClick = { navController.navigate(Screen.FavoritesScreen.route) },
            modifier = Modifier.width(240.dp).padding(vertical = 8.dp)
        ) {
            Text("Favorites")
        }

        // Botón 4: Lista de la Compra
        Button(
            onClick = { navController.navigate(Screen.ShoppingListScreen.route) },
            modifier = Modifier.width(240.dp).padding(vertical = 8.dp)
        ) {
            Text("Shopping List")
        }
    }
}