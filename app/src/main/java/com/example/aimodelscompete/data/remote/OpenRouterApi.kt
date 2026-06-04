package com.example.aimodelscompete.data.remote

import com.example.aimodelscompete.data.remote.dto.ChatRequest
import com.example.aimodelscompete.data.remote.dto.ChatResponse
import com.example.aimodelscompete.data.remote.dto.ModelsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterApi {

    @GET("api/v1/models")
    suspend fun getModels(
        @Header("Authorization") apiKey: String? = null
    ): ModelsResponse

    @POST("api/v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse

    companion object {
        const val BASE_URL = "https://openrouter.ai/"
    }
}
