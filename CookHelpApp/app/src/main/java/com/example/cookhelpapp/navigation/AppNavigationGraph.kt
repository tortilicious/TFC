package com.example.cookhelpapp.navigation

// --- Imports ---
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cookhelpapp.presentation.composable.MainMenuScreen
import com.example.cookhelpapp.presentation.composable.PlaceholderScreen
import com.example.cookhelpapp.presentation.composable.RecipeDetailScreen
import com.example.cookhelpapp.presentation.composable.RecipeSearchScreen
import com.example.cookhelpapp.presentation.composable.ShowRecipesScreen

// RecipeListMode y NavArgs ahora están definidos en Screen.kt (o en un archivo común de navegación)

/**
 * Define el grafo de navegación principal de la aplicación.
 */
@Composable
fun AppNavigationGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.MainMenu.route) {

        composable(route = Screen.MainMenu.route) {
            MainMenuScreen(navController = navController)
        }

        composable(route = Screen.NewRecipesInput.route) {
            // RecipeSearchScreen recogerá los filtros y navegará a ShowRecipesScreen
            // usando Screen.ShowRecipes.createRoute(mode = RecipeListMode.ALL_RECIPES, ...)
            RecipeSearchScreen(navController = navController)
        }

        // Ruta actualizada para ShowRecipesScreen
        composable(
            route = Screen.ShowRecipes.route + "/{${NavArgs.SCREEN_MODE}}" + // screenMode como path param
                    "?${NavArgs.SEARCH_TYPE}={${NavArgs.SEARCH_TYPE}}" +
                    "&${NavArgs.INGREDIENTS}={${NavArgs.INGREDIENTS}}" +
                    "&${NavArgs.CUISINE}={${NavArgs.CUISINE}}" +
                    "&${NavArgs.RANKING}={${NavArgs.RANKING}}",
            arguments = listOf(
                navArgument(NavArgs.SCREEN_MODE) { type = NavType.StringType }, // Obligatorio
                navArgument(NavArgs.SEARCH_TYPE) { // Opcional
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(NavArgs.INGREDIENTS) { // Opcional
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(NavArgs.CUISINE) { // Opcional
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(NavArgs.RANKING) { // Opcional
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            // El ShowRecipesViewModel se inyectará (ej. con hiltViewModel())
            // y leerá los argumentos del SavedStateHandle.
            ShowRecipesScreen(navController = navController)
        }


        composable(route = Screen.ZeroWasteRecipesInput.route) {
            PlaceholderScreen(screenName = "Aprovechamiento de Ingredientes")
        }
        composable(route = Screen.ShoppingListScreen.route) {
            PlaceholderScreen(screenName = "Lista de la Compra")
        }

        composable(
            route = Screen.RecipeDetail.route, // Definido como "recipe_detail_screen/{recipeId}"
            arguments = listOf(navArgument(NavArgs.RECIPE_ID) { type = NavType.IntType })
        ) {
            // RecipeDetailViewModel accederá a recipeId a través de SavedStateHandle
            RecipeDetailScreen(navController = navController)
        }
    }
}

// --- Funciones de extensión para NavController (opcional pero útil) ---
// Estas funciones se pueden colocar en un archivo separado dentro del paquete de navegación.

fun NavHostController.navigateToMainMenu() {
    this.navigate(Screen.MainMenu.route) {
        popUpTo(Screen.MainMenu.route) { inclusive = true }
    }
}

fun NavHostController.navigateToNewRecipesInput() {
    this.navigate(Screen.NewRecipesInput.route)
}

/**
 * Navega a la pantalla ShowRecipes con el modo y parámetros especificados.
 */
fun NavHostController.navigateToShowRecipes(
    mode: RecipeListMode,
    searchType: String? = null,
    ingredients: String? = null,
    cuisine: String? = null,
    ranking: String? = null // Mantener como String para la ruta
) {
    val route = Screen.ShowRecipes.createRoute(mode, searchType, ingredients, cuisine, ranking)
    this.navigate(route)
}

fun NavHostController.navigateToRecipeDetail(recipeId: Int) {
    this.navigate(Screen.RecipeDetail.createRoute(recipeId))
}

/**
 * Navega a ShowRecipesScreen en modo FAVORITE_RECIPES.
 */
fun NavHostController.navigateToFavorites() {
    this.navigate(Screen.ShowRecipes.createRoute(mode = RecipeListMode.FAVORITE_RECIPES))
}