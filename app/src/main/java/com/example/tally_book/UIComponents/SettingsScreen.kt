package com.example.tally_book.UIComponents

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext // 新增导入
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController
) {
    val context = LocalContext.current // 获取 Context
    // 使用 SharedPreferences 来存储和读取设置
    val sharedPreferences = remember { context.getSharedPreferences("ai_settings", Context.MODE_PRIVATE) }

    var nickname by remember { mutableStateOf(sharedPreferences.getString("nickname", "YORKIE") ?: "YORKIE") }
    var gender_list = listOf("保密","男","女","武装直升机","沃尔玛塑料袋")
    var gender by remember { mutableStateOf(sharedPreferences.getString("gender", gender_list[0]) ?: gender_list[0]) }
    var showgenderDropdown by remember { mutableStateOf(false) }
    var birthday by remember { mutableStateOf(sharedPreferences.getString("birthday", "未知") ?: "未知") }
    var aiSetting by remember { mutableStateOf(sharedPreferences.getString("aiSetting", "空") ?: "空") } // 从 SharedPreferences 加载


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // 使用主题背景色
    ) {
        TopAppBar(
            title = { Text("设置") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp) // 调整垂直间距
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // 使用主题色
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp) // 调整垂直间距
                    ) {
                        Text(
                            text = "个人信息",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        SettingItem(
                            label = "昵称",
                            value = nickname,
                            onValueChange = { nickname = it }
                        )

                        // 优化性别选择为下拉菜单，并放入 Row 中对齐
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
                        ) {
                            Text(
                                text = "性别",
                                modifier = Modifier.width(80.dp), // 设置与 SettingItem 相同的标签宽度
                                style = MaterialTheme.typography.bodyMedium
                            )
                            ExposedDropdownMenuBox(
                                expanded = showgenderDropdown,
                                onExpandedChange = { showgenderDropdown = !showgenderDropdown },
                                modifier = Modifier.weight(1f) // 让下拉框占据剩余空间
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(), // menuAnchor modifier
                                    readOnly = true,
                                    value = gender,
                                    onValueChange = { },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showgenderDropdown) },
                                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                                )
                                ExposedDropdownMenu(
                                    expanded = showgenderDropdown,
                                    onDismissRequest = { showgenderDropdown = false }
                                ) {
                                    gender_list.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text(selectionOption) },
                                            onClick = {
                                                gender = selectionOption
                                                showgenderDropdown = false
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                        )
                                    }
                                }
                            }
                        }

                        SettingItem(
                            label = "生日",
                            value = birthday,
                            onValueChange = { birthday = it }
                        )

                        SettingItem(
                            label = "AI设定",
                            value = aiSetting,
                            onValueChange = { aiSetting = it }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            // 保存设置到 SharedPreferences
                            with(sharedPreferences.edit()) {
                                putString("nickname", nickname)
                                putString("gender", gender)
                                putString("birthday", birthday)
                                putString("aiSetting", aiSetting)
                                apply()
                            }
                            // 可以在这里添加一个提示，例如 Toast
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("保存")
                    }

                    OutlinedButton(
                        onClick = {
                            // 重置为默认值并清除 SharedPreferences 中的特定值 (如果需要)
                            nickname = "YORKIE"
                            gender = "保密"
                            birthday = "未知"
                            aiSetting = "空"
                            with(sharedPreferences.edit()) {
                                remove("nickname")
                                remove("gender")
                                remove("birthday")
                                remove("aiSetting")
                                apply()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("取消")
                    }
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.width(80.dp), // 调整标签宽度
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors( // 美化输入框颜色
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp) // 添加圆角
        )
    }
}
