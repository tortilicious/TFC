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
// Importa tu ZeroWasteInputScreen real aquí
import com.example.cookhelpapp.presentation.composable.ZeroWasteInputScreen // <--- AÑADE ESTA IMPORTACIÓN
import com.example.cookhelpapp.presentation.composable.PlaceholderScreen // Puedes quitarla si ya no usas placeholders
import com.example.cookhelpapp.presentation.composable.RecipeDetailScreen
import com.example.cookhelpapp.presentation.composable.RecipeSearchScreen
import com.example.cookhelpapp.presentation.composable.ShowRecipesScreen


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
            RecipeSearchScreen(navController = navController)
        }

        composable(route = Screen.ZeroWasteRecipesInput.route) {
            ZeroWasteInputScreen(navController = navController)
        }

        composable(
            route = Screen.ShowRecipes.route + "/{${NavArgs.SCREEN_MODE}}" +
                    "?${NavArgs.INGREDIENTS}={${NavArgs.INGREDIENTS}}" +
                    "&${NavArgs.CUISINE}={${NavArgs.CUISINE}}" +
                    "&${NavArgs.RANKING}={${NavArgs.RANKING}}",
            arguments = listOf(
                navArgument(NavArgs.SCREEN_MODE) {
                    type = NavType.StringType
                },
                navArgument(NavArgs.INGREDIENTS) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(NavArgs.CUISINE) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(NavArgs.RANKING) {
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

// --- Funciones de extensión para NavController (sin cambios respecto a tu versión) ---

fun NavHostController.navigateToMainMenu() {
    this.navigate(Screen.MainMenu.route) {
        popUpTo(Screen.MainMenu.route) { inclusive = true }
    }
}

fun NavHostController.navigateToNewRecipesInput() {
    this.navigate(Screen.NewRecipesInput.route)
}

fun NavHostController.navigateToZeroWasteInput() {
    this.navigate(Screen.ZeroWasteRecipesInput.route)
}

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

fun NavHostController.navigateToShowRecipesByIngredientsSearch(
    ingredients: String,
    ranking: String
) {
    val route = Screen.ShowRecipes.createRoute(
        mode = ScreenDisplayMode.API_BY_INGREDIENTS_SEARCH,
        ingredients = ingredients,
        ranking = ranking
    )
    this.navigate(route)
}

fun NavHostController.navigateToFavorites() {
    this.navigate(Screen.ShowRecipes.createRoute(mode = ScreenDisplayMode.LOCAL_FAVORITES))
}

fun NavHostController.navigateToRecipeDetail(recipeId: Int) {
    this.navigate(Screen.RecipeDetail.createRoute(recipeId))
}
