package com.example.aimodelscompete.domain.use_case

import com.example.aimodelscompete.domain.repository.ModelRepository
import javax.inject.Inject

class ClearModelHistoryUseCase @Inject constructor(
    private val repository: ModelRepository
) {
    suspend operator fun invoke(modelId: String) {
        repository.clearHistoryForModel(modelId)
    }
}
