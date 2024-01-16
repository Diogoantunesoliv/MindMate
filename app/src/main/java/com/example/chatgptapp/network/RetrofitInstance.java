package com.example.chatgptapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
        val api: ChatGptService by lazy {
        Retrofit.Builder()
        .baseUrl("https://url-da-api") // Substitua pela URL base da API do ChatGPT
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ChatGptService::class.java)
        }
        }
