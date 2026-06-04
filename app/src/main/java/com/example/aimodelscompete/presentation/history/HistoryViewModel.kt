package com.example.aimodelscompete.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aimodelscompete.domain.model.ChatMessage
import com.example.aimodelscompete.domain.use_case.GetAllChatHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getAllChatHistoryUseCase: GetAllChatHistoryUseCase
) : ViewModel() {

    val history: StateFlow<List<ChatMessage>> = getAllChatHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
