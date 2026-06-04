package com.example.aimodelscompete.domain.use_case

import com.example.aimodelscompete.domain.model.ChatMessage
import com.example.aimodelscompete.domain.model.MessageRole
import com.example.aimodelscompete.domain.model.Model
import com.example.aimodelscompete.domain.repository.ModelRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ModelRepository
) {
    suspend operator fun invoke(modelId: String, content: String, history: List<ChatMessage>): Result<ChatMessage> {
        val userMessage = ChatMessage(role = MessageRole.USER, content = content)
        repository.saveChatMessage(modelId, userMessage)
        
        val fullMessages = history + userMessage
        return repository.getChatCompletion(modelId, fullMessages).onSuccess { assistantMessage ->
            repository.saveChatMessage(modelId, assistantMessage)
        }
    }
}
