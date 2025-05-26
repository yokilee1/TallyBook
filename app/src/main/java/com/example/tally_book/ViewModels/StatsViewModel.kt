package com.example.tally_book.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tally_book.DataModel.CategoryData
import com.example.tally_book.DataModel.Transaction
import com.example.tally_book.DataModel.TransactionType
import com.example.tally_book.Repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _categoryData = MutableStateFlow<List<CategoryData>>(emptyList())
    val categoryData: StateFlow<List<CategoryData>> = _categoryData.asStateFlow()

    private val _weeklyData = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val weeklyData: StateFlow<List<Pair<String, Double>>> = _weeklyData.asStateFlow()

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()

    init {
        loadStatsData()
    }

    private fun loadStatsData() {
        viewModelScope.launch {
            transactionRepository.getAllTransactions().collect { transactions ->
                processCategoryData(transactions)
                processWeeklyData(transactions)
                _totalExpense.value = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }
            }
        }
    }

    private fun processCategoryData(transactions: List<Transaction>) {
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
        val totalAmount = expenses.sumOf { it.amount }

        if (totalAmount == 0.0) {
            _categoryData.value = emptyList()
            return
        }

        val categoryColors = listOf(
            androidx.compose.ui.graphics.Color(0xFF2196F3),
            androidx.compose.ui.graphics.Color(0xFF4CAF50),
            androidx.compose.ui.graphics.Color(0xFFFF9800),
            androidx.compose.ui.graphics.Color(0xFFE91E63),
            androidx.compose.ui.graphics.Color(0xFF9C27B0),
            androidx.compose.ui.graphics.Color(0xFF00BCD4)
        )

        val categoryTotals = expenses.groupBy { it.category }
            .mapValues { it.value.sumOf { transaction -> transaction.amount } }
            .toList()
            .sortedByDescending { it.second }

        _categoryData.value = categoryTotals.mapIndexed { index, (category, amount) ->
            CategoryData(
                category = category,
                amount = amount,
                percentage = (amount / totalAmount * 100).toFloat(),
                color = categoryColors[index % categoryColors.size]
            )
        }
    }

    private fun processWeeklyData(transactions: List<Transaction>) {
        val calendar = Calendar.getInstance()
        val weekDays = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
        val weeklyExpenses = mutableMapOf<String, Double>()

        // Initialize with zeros
        weekDays.forEach { weeklyExpenses[it] = 0.0 }

        // Calculate expenses for current week
        val currentWeekStart = calendar.apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        transactions.filter {
            it.type == TransactionType.EXPENSE && it.date >= currentWeekStart
        }.forEach { transaction ->
            val transactionCalendar = Calendar.getInstance().apply {
                timeInMillis = transaction.date
            }
            val dayOfWeek = when (transactionCalendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "周一"
                Calendar.TUESDAY -> "周二"
                Calendar.WEDNESDAY -> "周三"
                Calendar.THURSDAY -> "周四"
                Calendar.FRIDAY -> "周五"
                Calendar.SATURDAY -> "周六"
                Calendar.SUNDAY -> "周日"
                else -> "周一"
            }
            weeklyExpenses[dayOfWeek] = weeklyExpenses[dayOfWeek]!! + transaction.amount
        }

        _weeklyData.value = weekDays.map { it to weeklyExpenses[it]!! }
    }
}
