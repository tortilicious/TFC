package com.example.cookhelpapp.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController // <--- CAMBIO AQUÍ: NavController a NavHostController
import com.example.cookhelpapp.navigation.Screen
import com.example.cookhelpapp.navigation.navigateToFavorites
import com.example.cookhelpapp.navigation.navigateToZeroWasteInput

/**
 * Pantalla principal de la aplicación que muestra las opciones del menú.
 *
 * @param navController Controlador de navegación para moverse a otras pantallas.
 * Ahora espera un NavHostController para compatibilidad con extensiones específicas.
 */
@Composable
fun MainMenuScreen(
    navController: NavHostController,
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
            modifier = Modifier
                .width(240.dp)
                .padding(vertical = 8.dp)
        ) {
            Text("New Recipes")
        }

        // Botón 2: Aprovechamiento (Zero-Waste Recipes)
        Button(
            onClick = { navController.navigateToZeroWasteInput() },
            modifier = Modifier
                .width(240.dp)
                .padding(vertical = 8.dp)
        ) {
            Text("Zero-Waste Recipes")
        }

        // Botón 3: Favoritos
        // Ahora navController es del tipo correcto (NavHostController) para la extensión.
        Button(
            onClick = { navController.navigateToFavorites() },
            modifier = Modifier
                .width(240.dp)
                .padding(vertical = 8.dp)
        ) {
            Text("Favorites")
        }

        // Botón 4: Lista de la Compra
        Button(
            onClick = { navController.navigate(Screen.ShoppingListScreen.route) },
            modifier = Modifier
                .width(240.dp)
                .padding(vertical = 8.dp)
        ) {
            Text("Shopping List")
        }
    }
}
