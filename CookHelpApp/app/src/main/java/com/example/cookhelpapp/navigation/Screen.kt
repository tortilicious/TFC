package com.example.cookhelpapp.navigation

/**
 * Define los diferentes modos en que se puede mostrar la lista de recetas.
 * Es buena práctica tenerlo en un archivo separado o junto a AppNavigationGraph.
 */
enum class RecipeListMode {
    /**
     * Indica que se deben cargar recetas desde una fuente remota (API),
     * usualmente aplicando filtros de búsqueda.
     */
    ALL_RECIPES,

    /**
     * Indica que se deben cargar las recetas favoritas guardadas localmente
     * en la base de datos.
     */
    FAVORITE_RECIPES
}

/**
 * Define las claves para los argumentos de navegación de forma centralizada.
 */
object NavArgs {
    const val SCREEN_MODE = "screenMode"
    const val RECIPE_ID = "recipeId"
    const val SEARCH_TYPE = "searchType"
    const val INGREDIENTS = "ingredients"
    const val CUISINE = "cuisine"
    const val RANKING = "ranking"
}

/**
 * Define las rutas de navegación de la aplicación.
 */
sealed class Screen(val route: String) {
    data object MainMenu : Screen("main_menu_screen")
    data object NewRecipesInput : Screen("new_recipes_input_screen")

    /**
     * Ruta para la pantalla que muestra listas de recetas.
     * Acepta un 'screenMode' como parte de la ruta y parámetros opcionales de búsqueda.
     */
    data object ShowRecipes : Screen("show_recipes_screen") { // Ruta base
        /**
         * Construye la cadena de ruta completa para navegar a la pantalla ShowRecipes.
         *
         * @param mode El modo de visualización (ALL_RECIPES o FAVORITE_RECIPES).
         * @param searchType Tipo de búsqueda (relevante para ALL_RECIPES).
         * @param ingredients Lista de ingredientes (relevante para ALL_RECIPES).
         * @param cuisine Tipo de cocina (relevante para ALL_RECIPES).
         * @param ranking Ranking (relevante para ALL_RECIPES).
         * @return La cadena de ruta completa con los argumentos.
         */
        fun createRoute(
            mode: RecipeListMode,
            searchType: String? = null,
            ingredients: String? = null,
            cuisine: String? = null,
            ranking: String? = null
        ): String {
            // screenMode es un parámetro de ruta obligatorio
            var path = "$route/${mode.name}" // ej: show_recipes_screen/ALL_RECIPES

            // Parámetros de consulta opcionales
            val queryParams = mutableListOf<String>()
            searchType?.let { queryParams.add("${NavArgs.SEARCH_TYPE}=$it") }
            ingredients?.let { queryParams.add("${NavArgs.INGREDIENTS}=$it") }
            cuisine?.let { queryParams.add("${NavArgs.CUISINE}=$it") }
            ranking?.let { queryParams.add("${NavArgs.RANKING}=$it") }

            if (queryParams.isNotEmpty()) {
                path += "?" + queryParams.joinToString("&")
            }
            return path
        }
    }

    data object ZeroWasteRecipesInput : Screen("zero_waste_input_screen")


    data object ShoppingListScreen : Screen("shopping_list_screen")

    data object RecipeDetail : Screen("recipe_detail_screen/{${NavArgs.RECIPE_ID}}") {
        /**
         * Construye la ruta completa para navegar a los detalles de una receta específica.
         * @param recipeId El ID de la receta a mostrar.
         * @return La cadena de ruta completa con el ID insertado.
         */
        fun createRoute(recipeId: Int): String {
            return route.replace("{${NavArgs.RECIPE_ID}}", recipeId.toString())
        }
    }
}