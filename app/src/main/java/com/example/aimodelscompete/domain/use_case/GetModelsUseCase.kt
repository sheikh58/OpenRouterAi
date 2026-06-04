package com.example.aimodelscompete.domain.use_case

import com.example.aimodelscompete.domain.model.Model
import com.example.aimodelscompete.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetModelsUseCase @Inject constructor(
    private val repository: ModelRepository
) {
    operator fun invoke(): Flow<List<Model>> {
        return repository.getModels()
    }
}
