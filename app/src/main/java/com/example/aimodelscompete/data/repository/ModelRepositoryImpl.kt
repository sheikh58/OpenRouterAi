package com.example.aimodelscompete.data.repository

import com.example.aimodelscompete.data.local.AiModelsDao
import com.example.aimodelscompete.data.local.SettingsManager
import com.example.aimodelscompete.data.mapper.toChatHistoryEntity
import com.example.aimodelscompete.data.mapper.toChatMessage
import com.example.aimodelscompete.data.mapper.toModel
import com.example.aimodelscompete.data.mapper.toModelEntity
import com.example.aimodelscompete.data.remote.OpenRouterApi
import com.example.aimodelscompete.data.remote.dto.ChatMessageDto
import com.example.aimodelscompete.data.remote.dto.ChatRequest
import com.example.aimodelscompete.domain.model.ChatMessage
import com.example.aimodelscompete.domain.model.MessageRole
import com.example.aimodelscompete.domain.model.Model
import com.example.aimodelscompete.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class ModelRepositoryImpl @Inject constructor(
    private val api: OpenRouterApi,
    private val dao: AiModelsDao,
    private val settingsManager: SettingsManager
) : ModelRepository {

    override fun getModels(): Flow<List<Model>> {
        return dao.getAllModels().map { entities ->
            entities.map { it.toModel() }
                .filter { it.id.endsWith(":free") && it.promptPrice == 0.0 && it.completionPrice == 0.0 }
        }
    }

    override suspend fun refreshModels(): Result<Unit> {
        return try {
            val apiKey = settingsManager.apiKey.first()
            if (apiKey.isNullOrBlank()) {
                return Result.failure(Exception("API Key not found. Please set it in Settings."))
            }
            
            val authHeader = "Bearer $apiKey"
            val response = withTimeout(15000) {
                api.getModels(authHeader)
            }
            
            val freeModels = response.data.filter { dto ->
                val isFreeId = dto.id.endsWith(":free")
                val pricing = dto.pricing
                val promptPrice = pricing?.prompt?.toDoubleOrNull() ?: 1.0
                val completionPrice = pricing?.completion?.toDoubleOrNull() ?: 1.0
                isFreeId && promptPrice == 0.0 && completionPrice == 0.0
            }
            
            val remoteModels = freeModels.map { it.toModelEntity() }
            
            // Sync favorites
            val currentLocal = dao.getModelsOnce()
            val favorites = currentLocal.filter { it.isFavorite }.associateBy { it.id }
            val updatedModels = remoteModels.map { 
                if (favorites.containsKey(it.id)) it.copy(isFavorite = true) else it
            }
            
            dao.deleteAllModels()
            dao.insertModels(updatedModels)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(modelId: String) {
        val models = dao.getModelsOnce()
        models.find { it.id == modelId }?.let { model ->
            dao.updateModel(model.copy(isFavorite = !model.isFavorite))
        }
    }

    override fun getChatHistory(modelId: String): Flow<List<ChatMessage>> {
        return dao.getHistoryForModel(modelId).map { entities ->
            entities.map { it.toChatMessage() }
        }
    }

    override suspend fun saveChatMessage(modelId: String, message: ChatMessage) {
        dao.insertChat(message.toChatHistoryEntity(modelId))
    }

    override suspend fun getChatCompletion(
        modelId: String,
        messages: List<ChatMessage>
    ): Result<ChatMessage> {
        return try {
            val apiKey = settingsManager.apiKey.first()
            if (apiKey.isNullOrBlank()) {
                return Result.failure(Exception("API Key not found. Please set it in Settings."))
            }

            val request = ChatRequest(
                model = modelId,
                messages = messages.map {
                    ChatMessageDto(
                        role = it.role.toLowerString(),
                        content = it.content
                    )
                }
            )
            val startTime = System.currentTimeMillis()
            val response = api.getChatCompletion("Bearer $apiKey", request)
            val endTime = System.currentTimeMillis()
            val latency = endTime - startTime

            val choice = response.choices.firstOrNull()
            if (choice != null) {
                val tokens = response.usage?.completion_tokens ?: 0
                val tokensPerSecond = if (latency > 0 && tokens > 0) {
                    (tokens.toDouble() / (latency.toDouble() / 1000.0))
                } else null

                val assistantMessage = ChatMessage(
                    role = MessageRole.ASSISTANT,
                    content = choice.message.content,
                    modelId = modelId,
                    latencyMs = latency,
                    tokensPerSecond = tokensPerSecond,
                    promptTokens = response.usage?.prompt_tokens,
                    completionTokens = response.usage?.completion_tokens
                )
                Result.success(assistantMessage)
            } else {
                Result.failure(Exception("No response from model"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllChatHistory(): Flow<List<ChatMessage>> {
        return dao.getAllHistory().map { entities ->
            entities.map { it.toChatMessage() }
        }
    }

    override suspend fun clearAllChatHistory() {
        dao.clearAllHistory()
    }

    override suspend fun clearHistoryForModel(modelId: String) {
        dao.clearHistoryForModel(modelId)
    }
}
