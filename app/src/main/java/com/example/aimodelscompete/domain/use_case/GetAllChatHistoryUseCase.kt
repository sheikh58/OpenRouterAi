package com.example.aimodelscompete.domain.use_case

import com.example.aimodelscompete.domain.model.ChatMessage
import com.example.aimodelscompete.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllChatHistoryUseCase @Inject constructor(
    private val repository: ModelRepository
) {
    operator fun invoke(): Flow<List<ChatMessage>> {
        return repository.getAllChatHistory()
    }
}
