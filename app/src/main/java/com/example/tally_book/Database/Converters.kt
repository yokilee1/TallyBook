package com.example.tally_book.Database

import androidx.room.TypeConverter
import com.example.tally_book.DataModel.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(type: String): TransactionType = TransactionType.valueOf(type)
}
