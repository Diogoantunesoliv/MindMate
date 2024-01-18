package com.example.chatgptapp.model

val chatRequest = ChatRequest(
    model = "gpt-3.5-turbo", // Use o modelo gpt-3.5-turbo
    messages = listOf(
        Message(
            role = "user",
            content = "Olá"
        )
    )
)
