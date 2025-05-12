package com.example.cookhelpapp.navigation

/**
 * Define los diferentes modos en que se puede mostrar la pantalla de lista de recetas,
 * indicando la fuente de datos y el tipo de búsqueda API si aplica.
 */
enum class ScreenDisplayMode {
    /** Carga recetas desde la API usando una búsqueda compleja (ingredientes, cocina, etc.). */
    API_COMPLEX_SEARCH,

    /** Carga recetas desde la API basándose en ingredientes y un ranking de aprovechamiento. */
    API_BY_INGREDIENTS_SEARCH,

    /** Carga las recetas favoritas guardadas localmente. */
    LOCAL_FAVORITES
}

/**
 * Define las claves para los argumentos de navegación de forma centralizada.
 */
object NavArgs {
    const val SCREEN_MODE = "screenDisplayMode"
    const val RECIPE_ID = "recipeId"
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
         * @param mode El ScreenDisplayMode que determina la fuente y tipo de datos.
         * @param ingredients Lista de ingredientes (relevante para API_COMPLEX_SEARCH y API_BY_INGREDIENTS_SEARCH).
         * @param cuisine Tipo de cocina (relevante para API_COMPLEX_SEARCH).
         * @param ranking Ranking (relevante para API_BY_INGREDIENTS_SEARCH, se pasa como String).
         * @return La cadena de ruta completa con los argumentos.
         */
        fun createRoute(
            mode: ScreenDisplayMode,
            ingredients: String? = null,
            cuisine: String? = null,
            ranking: String? = null
        ): String {
            var path = "$route/${mode.name}"
            val queryParams = mutableListOf<String>()

            when (mode) {
                ScreenDisplayMode.API_COMPLEX_SEARCH -> {
                    ingredients?.let { queryParams.add("${NavArgs.INGREDIENTS}=$it") }
                    cuisine?.let { queryParams.add("${NavArgs.CUISINE}=$it") }
                }

                ScreenDisplayMode.API_BY_INGREDIENTS_SEARCH -> {
                    ingredients?.let { queryParams.add("${NavArgs.INGREDIENTS}=$it") }
                    ranking?.let { queryParams.add("${NavArgs.RANKING}=$it") }
                }

                ScreenDisplayMode.LOCAL_FAVORITES -> { }
            }

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