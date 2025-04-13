package com.example.cookhelpapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ComplexSearchItemDto(
    val id: Int,
    val title: String,
    val image: String?
)