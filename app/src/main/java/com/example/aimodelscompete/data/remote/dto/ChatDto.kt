package com.example.aimodelscompete.data.remote.dto

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessageDto>
)

data class ChatMessageDto(
    val role: String,
    val content: String
)

data class ChatResponse(
    val id: String,
    val choices: List<ChatChoiceDto>,
    val usage: ChatUsageDto?
)

data class ChatUsageDto(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

data class ChatChoiceDto(
    val message: ChatMessageDto,
    val finish_reason: String?
)
