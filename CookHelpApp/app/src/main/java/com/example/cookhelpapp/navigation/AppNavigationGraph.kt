package com.example.cookhelpapp.navigation

// --- Imports ---
import androidx.compose.runtime.Composable
import androidx.navigation.NavType // Para definir tipos de argumentos
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument // Para definir argumentos
// Importa TODOS tus Composables de pantalla
import com.example.cookhelpapp.presentation.composable.MainMenuScreen
import com.example.cookhelpapp.presentation.composable.PlaceholderScreen
import com.example.cookhelpapp.presentation.composable.RecipeSearchScreen // La pantalla de inputs
import com.example.cookhelpapp.presentation.composable.ShowRecipesScreen  // La pantalla de resultados

/**
 * Define el grafo de navegación principal de la aplicación.
 * Configura el NavController y el NavHost con todas las rutas y sus Composables asociados.
 */
@Composable
fun AppNavigationGraph() {
    // 1. Crea y recuerda una instancia del NavController.
    val navController = rememberNavController()

    // 2. Define el NavHost, que es el contenedor donde se mostrarán las pantallas.
    //    'startDestination' es la primera pantalla que se muestra al abrir la app.
    NavHost(navController = navController, startDestination = Screen.MainMenu.route) {

        // Ruta para la pantalla del menú principal
        composable(route = Screen.MainMenu.route) {
            MainMenuScreen(navController = navController) // Le pasamos el NavController
        }

        // Ruta para la pantalla de entrada de filtros de "Nuevas Recetas"
        composable(route = Screen.NewRecipesInput.route) {
            // --- CORRECCIÓN: Pasar NavController a RecipeSearchScreen ---
            RecipeSearchScreen(navController = navController)
            // El ViewModel se inyecta con koinViewModel() dentro de RecipeSearchScreen
        }

        //  Definición de la ruta para mostrar los resultados de búsqueda ---
        composable(
            route = Screen.ShowRecipes.route, // La ruta base con placeholders para argumentos
            arguments = listOf( // Define los argumentos que esta ruta espera
                navArgument("searchType") { type = NavType.StringType },
                navArgument("ingredients") {
                    type = NavType.StringType
                    nullable = true // Puede ser null
                    defaultValue = "null" // Valor por defecto si no se pasa (string "null")
                },
                navArgument("cuisine") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "null"
                },
                navArgument("ranking") {
                    type = NavType.StringType // Se pasa como String
                    nullable = true
                    defaultValue = "null"
                }
            )
        ) { ShowRecipesScreen() }
        // --------------------------------------------------------------------------

        // Rutas para las pantallas placeholder (secciones futuras)
        composable(route = Screen.ZeroWasteRecipesInput.route) {
            // Cuando implementes esta, también necesitará NavController si navega
            PlaceholderScreen(screenName = "Aprovechamiento de Ingredientes")
        }
        composable(route = Screen.FavoritesScreen.route) {
            // Esta también necesitará NavController para ir a detalles, por ejemplo
            PlaceholderScreen(screenName = "Recetas Favoritas")
        }
        composable(route = Screen.ShoppingListScreen.route) {
            PlaceholderScreen(screenName = "Lista de la Compra")
        }

        // Aquí añadirías la ruta para RecipeDetail cuando la tengas
        // composable(
        //     route = Screen.RecipeDetail.route, // Ej: "recipe_detail_screen/{recipeId}"
        //     arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        // ) { backStackEntry ->
        //     val recipeId = backStackEntry.arguments?.getInt("recipeId")
        //     if (recipeId != null) {
        //         RecipeDetailScreen(recipeId = recipeId, navController = navController)
        //     } else {
        //         Text("Error: ID de receta no encontrado")
        //     }
        // }
    }
}
