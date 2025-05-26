package com.example.tally_book.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tally_book.DataModel.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 50")
    fun getRecentMessages(): Flow<List<ChatMessage>>

    @Insert
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()
}