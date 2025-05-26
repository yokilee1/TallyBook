package com.example.tally_book.DataModel

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "transactions")
@Parcelize
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val description: String,
    val date: Long = System.currentTimeMillis(),
    val type: TransactionType = TransactionType.EXPENSE,
    val receipt: String? = null
) : Parcelable

enum class TransactionType {
    INCOME, EXPENSE
}