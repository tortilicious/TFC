package com.example.cookhelpapp.domain.model

/**
 * Representa un resultado paginado de elementos.
 *
 * @param T El tipo de los elementos en la lista.
 * @property items La lista de elementos de la página actual.
 * @property totalResults El número total de resultados disponibles en la fuente de datos.
 * @property offset El offset utilizado para obtener esta página.
 * @property number El número de items solicitados para esta página.
 */
data class PagedResult<T>(
    val items: List<T>,
    val totalResults: Int,
    val offset: Int,
    val number: Int
)