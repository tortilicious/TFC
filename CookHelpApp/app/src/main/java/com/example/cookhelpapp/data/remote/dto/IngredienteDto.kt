package com.example.cookhelpapp.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class IngredienteDto (
     val id: Int,
     val name: String,
     val amount: Double?,
     val unit: String?

)