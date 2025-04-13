package com.example.cookhelpapp.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class ComplexSearchResponseDto(
    val results: List<ComplexSearchItemDto>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)
