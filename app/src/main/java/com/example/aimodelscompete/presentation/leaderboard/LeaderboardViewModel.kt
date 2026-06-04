package com.example.aimodelscompete.presentation.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aimodelscompete.domain.use_case.GetAllChatHistoryUseCase
import com.example.aimodelscompete.domain.use_case.GetModelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ModelStats(
    val modelId: String,
    val modelName: String,
    val avgLatency: Double,
    val avgThroughput: Double,
    val totalRequests: Int
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getAllChatHistoryUseCase: GetAllChatHistoryUseCase,
    private val getModelsUseCase: GetModelsUseCase
) : ViewModel() {

    val leaderboardData: StateFlow<List<ModelStats>> = combine(
        getAllChatHistoryUseCase(),
        getModelsUseCase()
    ) { history, models ->
        val modelNameMap = models.associate { it.id to it.name }

        history.filter { it.modelId != null && it.latencyMs != null }
            .groupBy { it.modelId!! }
            .map { (modelId, messages) ->
                val avgLatency = messages.mapNotNull { it.latencyMs }.average()
                val avgThroughput = messages.mapNotNull { it.tokensPerSecond }.average()
                ModelStats(
                    modelId = modelId,
                    modelName = modelNameMap[modelId] ?: modelId,
                    avgLatency = avgLatency,
                    avgThroughput = avgThroughput,
                    totalRequests = messages.size
                )
            }
            .sortedBy { it.avgLatency } // Lower latency is better
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
