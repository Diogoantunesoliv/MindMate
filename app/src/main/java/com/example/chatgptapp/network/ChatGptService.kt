package com.example.chatgptapp.network

import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.Call
import com.example.chatgptapp.model.ChatRequest
import com.example.chatgptapp.model.ChatResponse
import com.example.chatgptapp.BuildConfig

interface ChatGptService {
    @POST("/v1/chat/completions")
    @Headers("Authorization: Bearer ${BuildConfig.API_KEY}")
    fun postMessage(@Body request: ChatRequest): Call<ChatResponse>
}