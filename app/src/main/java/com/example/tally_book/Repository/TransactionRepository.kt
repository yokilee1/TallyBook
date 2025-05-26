package com.example.tally_book.Repository

import com.example.tally_book.Database.TransactionDao
import com.example.tally_book.DataModel.Transaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getAllTransactions() = transactionDao.getAllTransactions()

    fun getTransactionsByDateRange(startDate: Long, endDate: Long) =
        transactionDao.getTransactionsByDateRange(startDate, endDate)

    fun getCategoryTotals() = transactionDao.getCategoryTotals()

    suspend fun insertTransaction(transaction: Transaction) =
        transactionDao.insertTransaction(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)

    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction)
}