package com.example.tally_book.UIComponents

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.room.*
import com.example.tally_book.DataModel.TransactionType
import com.example.tally_book.ViewModels.MainViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import com.example.tally_book.DataModel.Transaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("美食") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) } // 新增：记录选择的交易类型

    val categories = listOf("美食", "交通", "娱乐", "购物", "住房", "其他")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        TopAppBar(
            title = { Text("添加交易") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer // 使用主题色
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // 调整整体内边距
            verticalArrangement = Arrangement.spacedBy(12.dp) // 调整元素之间的垂直间距
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp), // 调整卡片内部内边距
                    verticalArrangement = Arrangement.spacedBy(12.dp) // 调整卡片内部元素间距
                ) {
                    // 收入/支出切换
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly // 使按钮均匀分布
                    ) {
                        Button(
                            onClick = { selectedType = TransactionType.EXPENSE },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedType == TransactionType.EXPENSE) MaterialTheme.colorScheme.primary else Color.Gray
                            ),
                            modifier = Modifier.weight(1f).padding(end = 4.dp) // 调整按钮宽度和间距
                        ) {
                            Text("支出")
                        }
                        Button(
                            onClick = { selectedType = TransactionType.INCOME },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedType == TransactionType.INCOME) MaterialTheme.colorScheme.primary else Color.Gray
                            ),
                            modifier = Modifier.weight(1f).padding(start = 4.dp) // 调整按钮宽度和间距
                        ) {
                            Text("收入")
                        }
                    }

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("金额") },
                        placeholder = { Text("请输入金额") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = {
                            Text("￥", style = MaterialTheme.typography.bodyLarge)
                        }
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("类别") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("备注") },
                        placeholder = { Text("请输入备注（可选）") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }

            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && amountValue > 0) {
                        val transaction = Transaction(
                            amount = amountValue,
                            category = category,
                            description = description.ifBlank { "${category}${if(selectedType == TransactionType.EXPENSE) "消费" else "收入"}" }, // 根据类型生成默认备注
                            type = selectedType // 使用选择的类型
                        )
                        viewModel.addTransaction(transaction)
                        navController.popBackStack()
                    }
                },
                enabled = amount.toDoubleOrNull()?.let { it > 0 } == true,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("添加交易")
            }
        }
    }
}
