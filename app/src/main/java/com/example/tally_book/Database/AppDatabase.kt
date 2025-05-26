package com.example.tally_book.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tally_book.DataModel.ChatMessage
import com.example.tally_book.DataModel.Transaction

@Database(
    entities = [Transaction::class, ChatMessage::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun chatMessageDao(): ChatMessageDao
}
