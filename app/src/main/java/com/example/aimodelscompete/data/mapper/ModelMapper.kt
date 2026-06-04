package com.example.aimodelscompete.data.mapper

import com.example.aimodelscompete.data.local.entity.ChatHistoryEntity
import com.example.aimodelscompete.data.local.entity.ModelEntity
import com.example.aimodelscompete.data.remote.dto.ModelDto
import com.example.aimodelscompete.domain.model.ChatMessage
import com.example.aimodelscompete.domain.model.MessageRole
import com.example.aimodelscompete.domain.model.Model

fun ModelDto.toModelEntity(): ModelEntity {
    return ModelEntity(
        id = id,
        name = name,
        description = description ?: "",
        contextLength = contextLength,
        // Default to 1.0 if pricing is missing to ensure it doesn't pass the "free" check (0.0)
        promptPrice = pricing?.prompt?.toDoubleOrNull() ?: 1.0,
        completionPrice = pricing?.completion?.toDoubleOrNull() ?: 1.0
    )
}

fun ModelEntity.toModel(): Model {
    return Model(
        id = id,
        name = name,
        description = description,
        contextLength = contextLength,
        promptPrice = promptPrice,
        completionPrice = completionPrice,
        isFavorite = isFavorite
    )
}

fun Model.toModelEntity(): ModelEntity {
    return ModelEntity(
        id = id,
        name = name,
        description = description,
        contextLength = contextLength,
        promptPrice = promptPrice,
        completionPrice = completionPrice,
        isFavorite = isFavorite
    )
}

fun ChatHistoryEntity.toChatMessage(): ChatMessage {
    val messageRole = try {
        MessageRole.valueOf(role)
    } catch (e: Exception) {
        MessageRole.USER
    }
    return ChatMessage(
        role = messageRole,
        content = content,
        modelId = modelId,
        latencyMs = latencyMs,
        tokensPerSecond = tokensPerSecond,
        promptTokens = promptTokens,
        completionTokens = completionTokens,
        timestamp = timestamp
    )
}

fun ChatMessage.toChatHistoryEntity(modelId: String): ChatHistoryEntity {
    return ChatHistoryEntity(
        modelId = modelId,
        role = role.name,
        content = content,
        latencyMs = latencyMs,
        tokensPerSecond = tokensPerSecond,
        promptTokens = promptTokens,
        completionTokens = completionTokens,
        timestamp = timestamp
    )
}
