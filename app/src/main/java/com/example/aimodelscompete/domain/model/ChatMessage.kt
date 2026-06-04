package com.example.aimodelscompete.domain.model

data class ChatMessage(
    val role: MessageRole,
    val content: String,
    val modelId: String? = null,
    val latencyMs: Long? = null,
    val tokensPerSecond: Double? = null,
    val promptTokens: Int? = null,
    val completionTokens: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageRole {
    USER, ASSISTANT, SYSTEM;

    fun toLowerString() = name.lowercase()
}
