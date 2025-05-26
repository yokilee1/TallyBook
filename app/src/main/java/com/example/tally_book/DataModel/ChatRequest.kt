package com.example.tally_book.DataModel

data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val parameters: Parameters? = null // Optional parameters
)
data class ModelInfo(
    val name: String
)

data class Message(
    val role: String,
    val content: String
)

data class Parameters(
    val temperature: Double? = null // Example parameter
)