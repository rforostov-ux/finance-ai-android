package com.example.myaiproject.data.remote.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class ChatRequest(val message: String, val history: List<Any> = emptyList())
data class AnalysisResponse(val analysis: String)
data class ChatResponse(val response: String)

interface AiApi {
    @GET("ai/analyze")
    suspend fun analyze(
        @Header("Authorization") token: String
    ): AnalysisResponse

    @POST("ai/chat")
    suspend fun chat(
        @Header("Authorization") token: String,
        @Body body: ChatRequest
    ): ChatResponse
}