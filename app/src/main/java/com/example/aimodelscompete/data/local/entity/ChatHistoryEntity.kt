package com.example.aimodelscompete.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_history")
data class ChatHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val modelId: String,
    val role: String,
    val content: String,
    val latencyMs: Long? = null,
    val tokensPerSecond: Double? = null,
    val promptTokens: Int? = null,
    val completionTokens: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)
