package com.example.aimodelscompete.presentation.models

import com.example.aimodelscompete.domain.model.Model

data class ModelsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val models: List<ModelWithStats> = emptyList(),
    val filteredModels: List<ModelWithStats> = emptyList(),
    val selectedModelIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val error: String? = null
)

data class ModelWithStats(
    val model: Model,
    val avgLatency: Double? = null,
    val avgThroughput: Double? = null,
    val totalRequests: Int = 0
)
