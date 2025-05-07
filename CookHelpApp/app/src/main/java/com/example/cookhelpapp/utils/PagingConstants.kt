package com.example.cookhelpapp.utils

object PagingConstants {
    /**
     * El número de elementos (recetas, en este caso) que se solicitarán
     * a la API por página de forma predeterminada.
     */
    const val DEFAULT_RECIPE_NUMBER_PER_PAGE = 20

    /**
     * El offset inicial para la primera página de resultados.
     * Indica que no se deben saltar resultados al principio.
     */
    const val INITIAL_RECIPE_OFFSET = 0
}