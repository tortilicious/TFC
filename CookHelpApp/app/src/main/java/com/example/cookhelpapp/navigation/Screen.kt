package com.example.cookhelpapp.navigation


/**
 * Define las rutas de navegación de la aplicación.
 */
sealed class Screen(val route: String) {
    data object MainMenu : Screen("main_menu_screen")
    data object NewRecipesInput : Screen("new_recipes_input_screen")

    /**
     * Ruta para la pantalla que muestra los resultados de búsqueda.
     * Acepta parámetros opcionales para definir el tipo de búsqueda y los filtros aplicados.
     *
     * @property route La plantilla de la ruta con placeholders para los argumentos.
     */
    data object ShowRecipes : Screen("show_recipes_screen?type={searchType}&ingredients={ingredients}&cuisine={cuisine}&ranking={ranking}") {
        /**
         * Construye la cadena de ruta completa para navegar a la pantalla de resultados,
         * pasando los parámetros de búsqueda.
         * Los valores nulos para ingredientes y cocina se representan como la cadena literal "null".
         *
         * @param searchType El tipo de búsqueda a realizar (ej: "complex", "byIngredients").
         * @param ingredients Lista de ingredientes (opcional). Se unirán con comas si no es null.
         * @param cuisine Tipo de cocina (opcional).
         * @param ranking Ranking para la búsqueda por ingredientes (opcional).
         * @return La cadena de ruta completa con los argumentos para la navegación.
         */
        fun createRoute(
            searchType: String, // "complex" para Nuevas Recetas, "byIngredients" para Aprovechamiento
            ingredients: String? = null, // Ahora espera un String? (ya unido por comas o null)
            cuisine: String? = null,
            ranking: Int? = null // Solo para byIngredients
        ): String {
            val typeArg = searchType
            val ingredientsArg = ingredients ?: "null"
            val cuisineArg = cuisine ?: "null"
            val rankingArg = ranking?.toString() ?: "null"

            // Construye la ruta final.
            return "show_recipes_screen?type=$typeArg&ingredients=$ingredientsArg&cuisine=$cuisineArg&ranking=$rankingArg"
        }
    }

    data object ZeroWasteRecipesInput : Screen("zero_waste_input_screen")
    data object FavoritesScreen : Screen("favorites_screen")
    data object ShoppingListScreen : Screen("shopping_list_screen")
    // data object RecipeDetail : Screen("recipe_detail_screen/{recipeId}")
}
