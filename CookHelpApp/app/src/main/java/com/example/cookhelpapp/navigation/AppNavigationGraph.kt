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
            // Esta pantalla recoge filtros para API_COMPLEX_SEARCH
            RecipeSearchScreen(navController = navController)
        }

        composable(route = Screen.ZeroWasteRecipesInput.route) {
            // Esta pantalla recogerá ingredientes y ranking para API_BY_INGREDIENTS_SEARCH
            // Deberás crear esta pantalla, similar a RecipeSearchScreen.
            // Por ahora, un placeholder:
            PlaceholderScreen(screenName = "Entrada Aprovechamiento (ZeroWasteInputScreen)")
            // Ejemplo de cómo sería:
            // ZeroWasteInputScreen(navController = navController)
        }

        // Ruta actualizada para ShowRecipesScreen
        composable(
            route = Screen.ShowRecipes.route + "/{${NavArgs.SCREEN_MODE}}" + // screenDisplayMode como path param
                    "?${NavArgs.INGREDIENTS}={${NavArgs.INGREDIENTS}}" +
                    "&${NavArgs.CUISINE}={${NavArgs.CUISINE}}" +
                    "&${NavArgs.RANKING}={${NavArgs.RANKING}}", // Ranking como String
            arguments = listOf(
                navArgument(NavArgs.SCREEN_MODE) {
                    type = NavType.StringType
                }, // Obligatorio, será un nombre del enum ScreenDisplayMode
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
                navArgument(NavArgs.RANKING) { // Opcional, se pasa como String
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            ShowRecipesScreen(navController = navController)
        }

        composable(route = Screen.ShoppingListScreen.route) {
            PlaceholderScreen(screenName = "Lista de la Compra")
        }

        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(navArgument(NavArgs.RECIPE_ID) { type = NavType.IntType })
        ) {
            RecipeDetailScreen(navController = navController)
        }
    }
}

// --- Funciones de extensión para NavController (actualizadas) ---

fun NavHostController.navigateToMainMenu() {
    this.navigate(Screen.MainMenu.route) {
        popUpTo(Screen.MainMenu.route) { inclusive = true }
    }
}

/** Navega a la pantalla de entrada para búsqueda compleja (nuevas recetas). */
fun NavHostController.navigateToNewRecipesInput() {
    this.navigate(Screen.NewRecipesInput.route)
}

/** Navega a la pantalla de entrada para búsqueda por ingredientes (aprovechamiento). */
fun NavHostController.navigateToZeroWasteInput() {
    this.navigate(Screen.ZeroWasteRecipesInput.route)
}

/**
 * Navega a la pantalla ShowRecipes para búsqueda compleja.
 */
fun NavHostController.navigateToShowRecipesComplexSearch(
    ingredients: String?,
    cuisine: String?
) {
    val route = Screen.ShowRecipes.createRoute(
        mode = ScreenDisplayMode.API_COMPLEX_SEARCH,
        ingredients = ingredients,
        cuisine = cuisine

    )
    this.navigate(route)
}

/**
 * Navega a la pantalla ShowRecipes para búsqueda por ingredientes.
 */
fun NavHostController.navigateToShowRecipesByIngredientsSearch(
    ingredients: String, // Requerido para esta búsqueda
    ranking: String // Requerido para esta búsqueda (ej. "1" o "2")
) {
    val route = Screen.ShowRecipes.createRoute(
        mode = ScreenDisplayMode.API_BY_INGREDIENTS_SEARCH,
        ingredients = ingredients,
        ranking = ranking
        // cuisine no es relevante para byIngredients search en este ejemplo
    )
    this.navigate(route)
}

/**
 * Navega a ShowRecipesScreen en modo LOCAL_FAVORITES.
 */
fun NavHostController.navigateToFavorites() {
    this.navigate(Screen.ShowRecipes.createRoute(mode = ScreenDisplayMode.LOCAL_FAVORITES))
}

fun NavHostController.navigateToRecipeDetail(recipeId: Int) {
    this.navigate(Screen.RecipeDetail.createRoute(recipeId))
}