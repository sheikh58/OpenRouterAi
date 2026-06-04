package com.example.aimodelscompete.domain.model

data class Model(
    val id: String,
    val name: String,
    val description: String,
    val contextLength: Int,
    val promptPrice: Double,
    val completionPrice: Double,
    val isFavorite: Boolean = false
)
