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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.room.*
import com.example.tally_book.DataModel.CategoryData
import com.example.tally_book.ViewModels.StatsViewModel
import kotlinx.coroutines.flow.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    navController: NavController,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val categoryData by viewModel.categoryData.collectAsState()
    val weeklyData by viewModel.weeklyData.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        TopAppBar(
            title = { Text("财务报告") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Total Expense Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary // 使用主题主色作为背景
                    ),
                    shape = RoundedCornerShape(8.dp) // 添加圆角
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "总支出",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White // 文本颜色改为白色以适应深色背景
                        )
                        Text(
                            text = "￥${String.format("%.2f", totalExpense)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White, // 文本颜色改为白色
                            fontWeight = FontWeight.Bold // 加粗金额
                        )
                    }
                }
            }

            // Category Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "支出分类",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (categoryData.isNotEmpty()) {
                            CategoryPieChart(categoryData = categoryData)

                            Spacer(modifier = Modifier.height(16.dp))

                            categoryData.forEach { category ->
                                CategoryLegendItem(category = category)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "暂无数据",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // Weekly Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "本周开销情况",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        WeeklyChart(weeklyData = weeklyData)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryPieChart(categoryData: List<CategoryData>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = minOf(size.width, size.height) / 3

        var startAngle = -90f

        categoryData.forEach { category ->
            val sweepAngle = category.percentage * 3.6f

            drawArc(
                color = category.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            startAngle += sweepAngle
        }
    }
}

@Composable
fun CategoryLegendItem(category: CategoryData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // 使内容左右分散对齐
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(category.color, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = category.category,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${String.format("%.1f", category.percentage)}%",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "￥${String.format("%.2f", category.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun WeeklyChart(weeklyData: List<Pair<String, Double>>) {
    val maxAmount = weeklyData.maxOfOrNull { it.second } ?: 1.0

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            val barWidth = size.width / weeklyData.size * 0.6f
            val spacing = size.width / weeklyData.size * 0.4f
            val cornerRadius = CornerRadius(4f, 4f) // 添加圆角

            weeklyData.forEachIndexed { index, (day, amount) ->
                val barHeight = if (maxAmount > 0) (amount / maxAmount * size.height * 0.8f).toFloat() else 0f
                val x = index * (barWidth + spacing) + spacing / 2

                // 绘制柱子，使用圆角
                drawRoundRect(
                    color = Color.Blue,
                    topLeft = Offset(x, size.height - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = cornerRadius
                )

                // 添加金额文本标签 (需要额外的绘制逻辑，这里仅示意位置)
                // drawText(
                //     textMeasurer = textMeasurer, // 需要TextMeasurer
                //     text = "￥${String.format("%.2f", amount)}",
                //     topLeft = Offset(x + barWidth / 2, size.height - barHeight - 20f) // 示例位置
                // )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weeklyData.forEach { (day, _) ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
