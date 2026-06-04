package com.example.aimodelscompete.domain.repository

import com.example.aimodelscompete.domain.model.ChatMessage
import com.example.aimodelscompete.domain.model.Model
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    fun getModels(): Flow<List<Model>>
    suspend fun refreshModels(): Result<Unit>
    suspend fun toggleFavorite(modelId: String)
    
    fun getChatHistory(modelId: String): Flow<List<ChatMessage>>
    suspend fun saveChatMessage(modelId: String, message: ChatMessage)
    suspend fun getChatCompletion(modelId: String, messages: List<ChatMessage>): Result<ChatMessage>
    
    fun getAllChatHistory(): Flow<List<ChatMessage>>
    suspend fun clearAllChatHistory()
    suspend fun clearHistoryForModel(modelId: String)
}
