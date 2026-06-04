package com.example.aimodelscompete.data.repository

import com.example.aimodelscompete.data.local.AiModelsDao
import com.example.aimodelscompete.data.local.SettingsManager
import com.example.aimodelscompete.data.remote.OpenRouterApi
import com.example.aimodelscompete.data.remote.dto.ModelDto
import com.example.aimodelscompete.data.remote.dto.ModelsResponse
import com.example.aimodelscompete.data.remote.dto.PricingDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.SocketTimeoutException

class ModelRepositoryImplTest {

    private val api: OpenRouterApi = mockk()
    private val dao: AiModelsDao = mockk()
    private val settingsManager: SettingsManager = mockk()

    private lateinit var repository: ModelRepositoryImpl

    @Before
    fun setUp() {
        repository = ModelRepositoryImpl(api, dao, settingsManager)
        every { settingsManager.apiKey } returns flowOf("fake_key")
    }

    @Test
    fun `refreshModels should handle API timeout`() = runTest {
        // Given
        coEvery { api.getModels(any()) } throws SocketTimeoutException("Timeout")

        // When
        val result = repository.refreshModels()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is SocketTimeoutException)
    }

    @Test
    fun `refreshModels should filter only free models`() = runTest {
        // Given
        val freeModel = ModelDto(
            id = "google/gemma-2-9b-it:free",
            name = "Gemma 2 9B (free)",
            description = "Free model",
            contextLength = 8192,
            pricing = PricingDto("0", "0", "0", "0")
        )
        val paidModel = ModelDto(
            id = "openai/gpt-4",
            name = "GPT-4",
            description = "Paid model",
            contextLength = 8192,
            pricing = PricingDto("0.01", "0.03", "0", "0")
        )
        val modelsResponse = ModelsResponse(data = listOf(freeModel, paidModel))
        
        coEvery { api.getModels(any()) } returns modelsResponse
        coEvery { dao.getModelsOnce() } returns emptyList()
        coEvery { dao.deleteAllModels() } returns Unit
        coEvery { dao.insertModels(any()) } returns Unit

        // When
        val result = repository.refreshModels()

        // Then
        assertTrue(result.isSuccess)
        coVerify { 
            dao.insertModels(withArg { 
                assertTrue(it.size == 1)
                assertTrue(it[0].id == freeModel.id)
            }) 
        }
    }
}
