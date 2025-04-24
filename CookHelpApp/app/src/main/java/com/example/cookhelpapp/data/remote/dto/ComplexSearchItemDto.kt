package com.example.cookhelpapp.data.remote.dto

import kotlinx.serialization.Serializable


//  Dto para la búsqueda de recetas en el endpoint ComplexSearch
@Serializable
data class ComplexSearchItemDto(
    val id: Int,
    val title: String,
    val image: String?
)