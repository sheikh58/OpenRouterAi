package com.example.aimodelscompete.domain.use_case

import com.example.aimodelscompete.domain.repository.ModelRepository
import javax.inject.Inject

class ClearHistoryUseCase @Inject constructor(
    private val repository: ModelRepository
) {
    suspend operator fun invoke() {
        repository.clearAllChatHistory()
    }
}
