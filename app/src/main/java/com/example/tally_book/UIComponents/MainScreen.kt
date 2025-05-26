package com.example.tally_book.UIComponents

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tally_book.ViewModels.MainViewModel
import com.example.tally_book.DataModel.ChatMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "AI记账本",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black
                )
            },
            actions = {
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "菜单"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer // 使用主题色
            )
        )

        // Chat Area
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(chatMessages) { message ->
                ChatMessageItem(message = message)
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            modifier = Modifier.padding(end = 48.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI正在思考...")
                            }
                        }
                    }
                }
            }
        }

        // Input Area
        Surface(
            color = Color.White, // 使用主题色
            shadowElevation = 8.dp
        ) {
            // 更多操作按钮
            var showQuickActions by remember { mutableStateOf(false) }

            Column {

                // Input Field
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("请输入内容") },
                        singleLine = true // 考虑允许多行输入，微信是支持的
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // 发送按钮
                    Button(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(messageText)
                                messageText = ""
                            }
                        },
                        enabled = messageText.isNotBlank() && !isLoading
                    ) {
                        Text("发送")
                    }

                    Spacer(modifier = Modifier.width(8.dp))


                    IconButton(onClick = { showQuickActions = !showQuickActions }) {
                        Icon(Icons.Default.Add, contentDescription = "更多操作")
                    }
                }

                // 根据 showQuickActions 状态显示快捷操作
                if (showQuickActions) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp), // 调整边距
                        horizontalArrangement = Arrangement.SpaceAround // 可以调整排列方式
                    ) {
                        item {
                            QuickActionButton(
                                icon = Icons.Default.Search,
                                label = "扫描收据",
                                onClick = { /* Handle camera */ }
                            )
                        }
                        item {
                            QuickActionButton(
                                icon = Icons.Default.Add,
                                label = "添加交易",
                                onClick = { navController.navigate("add_transaction") }
                            )
                        }
                        item {
                            QuickActionButton(
                                icon = Icons.Default.Info,
                                label = "财务报告",
                                onClick = { navController.navigate("stats") }
                            )
                        }
                        item {
                            QuickActionButton(
                                icon = Icons.Default.DateRange,
                                label = "历史账单",
                                onClick = { navController.navigate("history") }
                            )
                        }
                        item {
                            QuickActionButton(
                                icon = Icons.Default.Settings,
                                label = "设置",
                                onClick = { navController.navigate("settings") }
                            )
                        }
                        item {
                            QuickActionButton(
                                icon = Icons.Default.Notifications,
                                label = "帮助",
                                onClick = { /* Handle help */ }
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp), // 调整内边距
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        // TODO: 在这里根据 message.isFromUser 添加头像
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = if (message.isFromUser) 16.dp else 4.dp,
                topEnd = if (message.isFromUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ), // 更圆角的聊天气泡
            colors = CardDefaults.cardColors(
                containerColor = if (message.isFromUser)
                    MaterialTheme.colorScheme.primary // 使用主题色
                else
                    MaterialTheme.colorScheme.surfaceVariant // AI消息使用不同的表面颜色
            )
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), // 调整文本内边距
                color = if (message.isFromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge // 可以考虑使用 bodyLarge 获得更清晰的文本
            )
        }
        // TODO: 在这里根据 !message.isFromUser 添加头像 (如果AI也有头像)
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Card(
            modifier = Modifier.size(48.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF0F0F0)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF2196F3)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}