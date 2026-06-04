package com.example.aimodelscompete.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aimodelscompete.domain.use_case.GetModelsUseCase
import com.example.aimodelscompete.domain.use_case.ToggleFavoriteUseCase
import com.example.aimodelscompete.domain.use_case.GetAllChatHistoryUseCase
import com.example.aimodelscompete.domain.use_case.RefreshModelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModelsViewModel @Inject constructor(
    private val getModelsUseCase: GetModelsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getAllChatHistoryUseCase: GetAllChatHistoryUseCase,
    private val refreshModelsUseCase: RefreshModelsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ModelsState())
    val state = _state.asStateFlow()

    init {
        observeModelsAndStats()
        refresh() // Initial load
    }

    private fun observeModelsAndStats() {
        viewModelScope.launch {
            combine(
                getModelsUseCase().distinctUntilChanged(),
                getAllChatHistoryUseCase().distinctUntilChanged()
            ) { models, history ->
                val historyByModel = history.filter { it.modelId != null && it.latencyMs != null }
                    .groupBy { it.modelId!! }

                models.map { model ->
                    val stats = historyByModel[model.id]
                    ModelWithStats(
                        model = model,
                        avgLatency = stats?.mapNotNull { it.latencyMs }?.average(),
                        avgThroughput = stats?.mapNotNull { it.tokensPerSecond }?.average(),
                        totalRequests = stats?.size ?: 0
                    )
                }
            }
            .flowOn(Dispatchers.Default)
            .collect { modelsWithStats ->
                _state.update { it.copy(
                    models = modelsWithStats,
                    filteredModels = filterList(modelsWithStats, it.searchQuery),
                    isLoading = false
                ) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(
                isRefreshing = true,
                isLoading = it.models.isEmpty()
            ) }
            
            val result = refreshModelsUseCase()
            
            _state.update { it.copy(
                isRefreshing = false,
                isLoading = false,
                error = result.exceptionOrNull()?.message
            ) }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { 
            it.copy(
                searchQuery = query,
                filteredModels = filterList(it.models, query)
            )
        }
    }

    private fun filterList(list: List<ModelWithStats>, query: String): List<ModelWithStats> {
        return if (query.isBlank()) {
            list
        } else {
            list.filter {
                it.model.name.contains(query, ignoreCase = true) ||
                        it.model.id.contains(query, ignoreCase = true)
            }
        }
    }

    fun onFavoriteClick(modelId: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(modelId)
        }
    }
}
