package com.example.aimodelscompete.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "models")
data class ModelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val contextLength: Int,
    val promptPrice: Double,
    val completionPrice: Double,
    val isFavorite: Boolean = false
)
