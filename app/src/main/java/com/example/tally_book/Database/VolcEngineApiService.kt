package com.example.tally_book.Database

import com.example.tally_book.DataModel.ChatRequest
import com.example.tally_book.DataModel.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Retrofit interface for the API
interface VolcEngineApiService {
    @POST("api/v3/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse
}