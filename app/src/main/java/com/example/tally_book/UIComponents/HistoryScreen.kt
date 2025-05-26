package com.example.tally_book.UIComponents

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.room.*
import com.example.tally_book.ViewModels.MainViewModel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.tally_book.DataModel.Transaction
import com.example.tally_book.DataModel.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        TopAppBar(
            title = { Text("历史账单") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer // 使用主题色
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(transaction = transaction)
            }

            if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂无交易记录",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (transaction.type == TransactionType.EXPENSE)
                            Color(0xFFFFEBEE) else Color(0xFFE8F5E8),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (transaction.category) {
                        "美食" -> Icons.Default.Favorite
                        "交通" -> Icons.Default.LocationOn
                        "娱乐" -> Icons.Default.Face
                        "购物" -> Icons.Default.ShoppingCart
                        "住房" -> Icons.Default.Home
                        else -> Icons.Default.AddCircle
                    },
                    contentDescription = transaction.category,
                    tint = if (transaction.type == TransactionType.EXPENSE)
                        Color(0xFFE57373) else Color(0xFF81C784)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transaction Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                        .format(Date(transaction.date)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            // Amount
            Text(
                text = "${if (transaction.type == TransactionType.EXPENSE) "-" else "+"}￥${String.format("%.2f", transaction.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (transaction.type == TransactionType.EXPENSE)
                    Color(0xFFE57373) else Color(0xFF4CAF50)
            )
        }
    }
}