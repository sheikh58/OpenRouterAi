package com.example.aimodelscompete.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ModelDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("context_length") val contextLength: Int,
    @SerializedName("pricing") val pricing: PricingDto?
)

data class PricingDto(
    @SerializedName("prompt") val prompt: String,
    @SerializedName("completion") val completion: String,
    @SerializedName("request") val request: String,
    @SerializedName("image") val image: String
)

data class ModelsResponse(
    @SerializedName("data") val data: List<ModelDto>
)
