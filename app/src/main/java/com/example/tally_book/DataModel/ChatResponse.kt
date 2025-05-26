package com.example.tally_book.DataModel

data class ChatResponse(
    val choices: List<Choice>,
    val usage: Usage
)

data class Choice(
    val message: Message,
    val finish_reason: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)