package com.example.chatgptapp.network

import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.Call
import com.example.chatgptapp.model.ChatMessage
import com.example.chatgptapp.model.ChatResponse

interface ChatGptService {
    @POST("/caminho/do/endpoint")
    fun postMessage(@Body message: ChatMessage): Call<ChatResponse>
}
