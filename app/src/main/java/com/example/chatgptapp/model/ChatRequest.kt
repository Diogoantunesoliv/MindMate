package com.example.chatgptapp.model

data class ChatRequest(
    val model: String,
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)

fun createChatRequest(inputText: String): ChatRequest {
    return ChatRequest(
        model = "gpt-3.5-turbo", // Ajuste o modelo conforme necessário
        messages = listOf(Message(role = "user", content = inputText))
    )
}
