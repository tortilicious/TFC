package com.example.cookhelpapp.data.local.db

import androidx.room.TypeConverter

/**
 * Conversores de tipo para Room.
 * Permiten a Room almacenar tipos complejos (como List<String>) en columnas simples (como String).
 */
object Converters {
    /**
     * Convierte una lista de cadenas en una cadena separada por comas.
     * @param list Lista de cadenas a convertir.
     * @return Cadena separada por comas o null si la lista es null.
     */
    @TypeConverter
    @JvmStatic
    fun fromList(list: List<String>?): String? = list?.joinToString(",")

    /**
     * Convierte una cadena separada por comas en una lista de cadenas.
     * @param string Cadena separada por comas a convertir.
     * @return Lista de cadenas o null si la cadena es null.
     */
    @TypeConverter
    @JvmStatic
    fun toList(string: String?): List<String>? = string?.split(",")?.map { it.trim() }

}