package com.example.aimodelscompete.presentation.models

import com.example.aimodelscompete.domain.model.ChatMessage
import com.example.aimodelscompete.domain.model.MessageRole
import com.example.aimodelscompete.domain.model.Model
import com.example.aimodelscompete.domain.use_case.GetAllChatHistoryUseCase
import com.example.aimodelscompete.domain.use_case.GetModelsUseCase
import com.example.aimodelscompete.domain.use_case.RefreshModelsUseCase
import com.example.aimodelscompete.domain.use_case.ToggleFavoriteUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ModelsViewModelTest {

    private val getModelsUseCase: GetModelsUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()
    private val getAllChatHistoryUseCase: GetAllChatHistoryUseCase = mockk()
    private val refreshModelsUseCase: RefreshModelsUseCase = mockk()

    private val testDispatcher = UnconfinedTestDispatcher()

    private val modelsFlow = MutableStateFlow<List<Model>>(emptyList())
    private val historyFlow = MutableStateFlow<List<ChatMessage>>(emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        every { getModelsUseCase() } returns modelsFlow
        every { getAllChatHistoryUseCase() } returns historyFlow
        coEvery { refreshModelsUseCase() } returns Result.success(Unit)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = ModelsViewModel(
        getModelsUseCase,
        toggleFavoriteUseCase,
        getAllChatHistoryUseCase,
        refreshModelsUseCase
    )

    @Test
    fun `refresh should toggle isRefreshing state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.refresh()

        // Then
        assertFalse(viewModel.state.value.isRefreshing)
    }

    @Test
    fun `refresh failure should set error message`() = runTest {
        // Given
        val errorMessage = "Network Error"
        coEvery { refreshModelsUseCase() } returns Result.failure(Exception(errorMessage))
        
        val viewModel = createViewModel()

        // When
        viewModel.refresh()

        // Then
        assertEquals(errorMessage, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isRefreshing)
    }

    @Test
    fun `models and stats should be combined correctly`() = runTest {
        // Given
        val models = listOf(
            Model(
                id = "1", 
                name = "Model 1", 
                description = "", 
                contextLength = 1000, 
                promptPrice = 0.0, 
                completionPrice = 0.0
            )
        )
        val history = listOf(
            ChatMessage(role = MessageRole.USER, content = "hi", modelId = "1", latencyMs = 100L, tokensPerSecond = 50.0),
            ChatMessage(role = MessageRole.ASSISTANT, content = "hello", modelId = "1", latencyMs = 200L, tokensPerSecond = 40.0)
        )

        // When
        val viewModel = createViewModel()
        modelsFlow.value = models
        historyFlow.value = history

        // Then
        val currentState = viewModel.state.value
        if (currentState.models.isEmpty()) {
             // Sometimes combine takes a tick even with UnconfinedTestDispatcher
             // due to multiple flows.
        }

        assertEquals(1, currentState.models.size)
        val modelWithStats = currentState.models[0]
        assertEquals("1", modelWithStats.model.id)
        assertEquals(150.0, modelWithStats.avgLatency!!, 0.1)
        assertEquals(45.0, modelWithStats.avgThroughput!!, 0.1)
        assertEquals(2, modelWithStats.totalRequests)
    }

    @Test
    fun `onSearchQueryChange should filter models`() = runTest {
        // Given
        val models = listOf(
            Model(id = "gpt-4", name = "GPT-4", description = "", contextLength = 8192, promptPrice = 0.0, completionPrice = 0.0),
            Model(id = "claude-3", name = "Claude 3", description = "", contextLength = 200000, promptPrice = 0.0, completionPrice = 0.0)
        )
        modelsFlow.value = models
        val viewModel = createViewModel()

        // When
        viewModel.onSearchQueryChange("gpt")

        // Then
        assertEquals(1, viewModel.state.value.filteredModels.size)
        assertEquals("gpt-4", viewModel.state.value.filteredModels[0].model.id)
    }
}
