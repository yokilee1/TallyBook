package com.example.tally_book.Repository

import android.content.Context // 新增导入
import android.util.Log
import com.example.tally_book.DataModel.ChatRequest
import com.example.tally_book.DataModel.Message
import com.example.tally_book.DataModel.ModelInfo
import com.example.tally_book.DataModel.Transaction
import com.example.tally_book.DataModel.TransactionType
import com.example.tally_book.Database.VolcEngineApiService
import com.example.tally_book.util.utilConstants.Companion.API_KEY
import com.example.tally_book.util.utilConstants.Companion.API_URL
import com.example.tally_book.util.utilConstants.Companion.MODEL_NAME
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import com.google.gson.Gson //确保已导入
import dagger.hilt.android.qualifiers.ApplicationContext // 新增导入
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@Singleton
class AIRepository @Inject constructor(
    @ApplicationContext private val context: Context // 注入 ApplicationContext
) {

    private val apiKey = API_KEY // Your API key
    private val modelName = MODEL_NAME // Replace with your actual model name, e.g., "doubao-pro-32k"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 可以设置为 NONE, BASIC, HEADERS, BODY
    }
    // Create OkHttpClient with increased timeouts
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(300, TimeUnit.SECONDS) // 连接超时时间
        .readTimeout(300, TimeUnit.SECONDS)    // 读取超时时间
        .writeTimeout(300, TimeUnit.SECONDS)   // 写入超时时间
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(API_URL) // Corrected base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val volcEngineApiService = retrofit.create(VolcEngineApiService::class.java)

    suspend fun processUserQuery(query: String, transactions: List<Transaction>): String { // Changed return type to String
        // 从 SharedPreferences 读取 AI设定
        val sharedPreferences = context.getSharedPreferences("ai_settings", Context.MODE_PRIVATE)
        val userAiSetting = sharedPreferences.getString("aiSetting", "You are a helpful accounting assistant.") ?: "You are a helpful accounting assistant."

        // 1. 格式化交易数据作为上下文
        val transactionContext = formatTransactionsForAI(transactions)

        // 2. 构建包含交易上下文和用户AI设定的系统消息
        val systemMessageContent = """
        请你以
        $userAiSetting
        的口吻，回答用户的问题：
        Here is a summary of recent transactions:
        $transactionContext
        Please answer the user's questions based on this information and their query.
        """.trimIndent()

        val request = ChatRequest(
            model = modelName,
            messages = listOf(
                Message(role = "system", content = systemMessageContent),
                Message(role = "user", content = query)
            )
        )

        return try {
            val response = volcEngineApiService.getChatCompletion(apiKey, request)
            response.choices.firstOrNull()?.message?.content ?: "Sorry, I couldn't get a response."
        } catch (e: Exception) {
            Log.e("AIRepository", "API call failed", e)
            val fallbackResponse = getLocalFallbackResponse(query, transactions)
            if (fallbackResponse != null) {
                fallbackResponse // 返回本地降级处理的结果
            } else {
                // 根据异常类型提供更具体的错误反馈 (同上面的 when(e) ...)
                when (e) {
                    is java.net.UnknownHostException -> "网络连接失败，请检查您的网络设置。如果您想进行本地查询，请尝试更简单的问题，例如“本月支出”。"
                    is java.net.SocketTimeoutException -> "请求超时，请检查您的网络连接或稍后重试。如果您想进行本地查询，请尝试更简单的问题。"
                    // ... 其他错误处理
                    else -> "抱歉，AI助手遇到未知错误，请稍后再试。 (${e.message})"
                }
            }
        }
    }

    // 新增辅助方法：格式化交易数据
    private fun formatTransactionsForAI(transactions: List<Transaction>, maxTransactions: Int = 10): String {
        if (transactions.isEmpty()) {
            return "No transactions recorded yet."
        }
        // 可以选择最近的N条，或者按日期筛选等，这里简单取最新的N条
        val recentTransactions = transactions.takeLast(maxTransactions).reversed()
        val formatted = recentTransactions.joinToString("\n") { transaction ->
            val type = if (transaction.type == TransactionType.EXPENSE) "Expense" else "Income"
            val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date(transaction.date))
            "- $date: $type of ${transaction.amount} in ${transaction.category} (${transaction.description})"
        }
        return if (formatted.isBlank()) "No recent transactions to show." else formatted
    }

    // 添加本地降级处理逻辑
    private fun getLocalFallbackResponse(query: String, transactions: List<Transaction>): String? {
        return when {
            query.contains("本月") || query.contains("这个月") -> {
                val calendar = Calendar.getInstance()
                val currentMonth = calendar.get(Calendar.MONTH)
                val monthTransactions = transactions.filter { transaction ->
                    val transactionCalendar = Calendar.getInstance().apply {
                        timeInMillis = transaction.date
                    }
                    transactionCalendar.get(Calendar.MONTH) == currentMonth
                }

                when {
                    query.contains("餐饮") || query.contains("吃饭") -> {
                        val foodExpenses = monthTransactions
                            .filter { it.category == "美食" || it.category == "餐饮" }
                            .sumOf { it.amount }
                        "本月餐饮支出：￥${String.format("%.2f", foodExpenses)}"
                    }
                    query.contains("交通") -> {
                        val transportExpenses = monthTransactions
                            .filter { it.category == "交通" }
                            .sumOf { it.amount }
                        "本月交通支出：￥${String.format("%.2f", transportExpenses)}"
                    }
                    query.contains("购物") -> {
                        val shoppingExpenses = monthTransactions
                            .filter { it.category == "购物" }
                            .sumOf { it.amount }
                        "本月购物支出：￥${String.format("%.2f", shoppingExpenses)}"
                    }
                    query.contains("娱乐") -> {
                        val entertainmentExpenses = monthTransactions
                            .filter { it.category == "娱乐" }
                            .sumOf { it.amount }
                        "本月娱乐支出：￥${String.format("%.2f", entertainmentExpenses)}"
                    }
                    query.contains("住房")->{
                        val housingExpenses = monthTransactions
                            .filter { it.category == "住房" }
                            .sumOf { it.amount }
                        "本月住房支出：￥${String.format("%.2f", housingExpenses)}"
                    }
                    else -> {
                        val totalExpenses = monthTransactions
                            .filter { it.type == TransactionType.EXPENSE }
                            .sumOf { it.amount }
                        "本月总支出：￥${String.format("%.2f", totalExpenses)}"
                    }
                }
            }
            query.contains("建议") || query.contains("如何") -> {
                val expensesByCategory = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .groupBy { it.category }
                    .mapValues { it.value.sumOf { transaction -> transaction.amount } }

                val maxCategory = expensesByCategory.maxByOrNull { it.value }
                if (maxCategory != null) {
                    "建议减少${maxCategory.key}支出，这是您最大的开销类别（￥${String.format("%.2f", maxCategory.value)}）"
                } else {
                    "您的消费习惯很好，建议继续保持记账的好习惯！"
                }
            }
            else -> "我理解您想了解财务情况。请尝试问：本月餐饮花了多少？或给我一些省钱建议"

        }
    }
}
