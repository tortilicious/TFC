package com.example.cookhelpapp.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class RecetaGeneralDto(
    val id: Int,
    val title: String,
    val image: String?  // URL de la imagen de la receta en caso de haberla
)
