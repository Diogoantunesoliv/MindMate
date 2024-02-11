package com.example.chatgptapp.model

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
