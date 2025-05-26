package com.example.tally_book.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tally_book.DataModel.ChatMessage
import com.example.tally_book.DataModel.Transaction
import com.example.tally_book.DataModel.TransactionType
import com.example.tally_book.Database.ChatMessageDao
import com.example.tally_book.Repository.AIRepository
import com.example.tally_book.Repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val aiRepository: AIRepository,
    private val chatMessageDao: ChatMessageDao
) : ViewModel() {

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            launch {
                transactionRepository.getAllTransactions().collect {
                    _transactions.value = it
                }
            }
            launch {
                chatMessageDao.getRecentMessages().collect {
                    _chatMessages.value = it.reversed()
                }
            }
        }
    }

//    fun sendMessage(message: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//
//            // Add user message
//            val userMessage = ChatMessage(content = message, isFromUser = true)
//            chatMessageDao.insertMessage(userMessage)
//
//            // Process AI response
//            val aiResponse = aiRepository.processUserQuery(message, _transactions.value)
//            val aiMessage = ChatMessage(content = aiResponse as String, isFromUser = false)
//            chatMessageDao.insertMessage(aiMessage)
//
//            _isLoading.value = false
//        }
//    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            _isLoading.value = true

            // Add user message
            val userMessage = ChatMessage(content = message, isFromUser = true)
            chatMessageDao.insertMessage(userMessage)

            // Process AI response
            val aiResponseContent = aiRepository.processUserQuery(message, _transactions.value) // aiResponse is now a String
            val aiMessage = ChatMessage(content = aiResponseContent, isFromUser = false)
            chatMessageDao.insertMessage(aiMessage)

            _isLoading.value = false
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.insertTransaction(transaction)

            // Add system message
            val systemMessage = ChatMessage(
                content = "已添加${if (transaction.type == TransactionType.EXPENSE) "支出" else "收入"}：${transaction.category} ￥${String.format("%.2f", transaction.amount)}",
                isFromUser = false
            )
            chatMessageDao.insertMessage(systemMessage)
        }
    }
}