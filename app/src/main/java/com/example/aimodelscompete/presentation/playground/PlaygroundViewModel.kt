package com.example.aimodelscompete.presentation.playground

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aimodelscompete.domain.use_case.ClearModelHistoryUseCase
import com.example.aimodelscompete.domain.use_case.GetChatHistoryUseCase
import com.example.aimodelscompete.domain.use_case.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaygroundViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val clearModelHistoryUseCase: ClearModelHistoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(PlaygroundState())
    val state = _state.asStateFlow()

    init {
        savedStateHandle.get<String>("modelId")?.let { modelId ->
            _state.update { it.copy(modelId = modelId) }
            observeChatHistory(modelId)
        }
    }

    private fun observeChatHistory(modelId: String) {
        viewModelScope.launch {
            getChatHistoryUseCase(modelId).collect { history ->
                _state.update { it.copy(messages = history) }
            }
        }
    }

    fun onInputTextChange(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val currentText = _state.value.inputText
        if (currentText.isBlank() || _state.value.isLoading) return

        _state.update { 
            it.copy(
                inputText = "",
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            sendMessageUseCase(_state.value.modelId, currentText, _state.value.messages)
                .onFailure { error ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error"
                        )
                    }
                }
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            clearModelHistoryUseCase(_state.value.modelId)
        }
    }
}
