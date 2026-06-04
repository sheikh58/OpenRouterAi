package com.example.aimodelscompete.presentation.playground

import com.example.aimodelscompete.domain.model.ChatMessage

data class PlaygroundState(
    val modelId: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
