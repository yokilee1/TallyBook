package com.example.tally_book.UIComponents

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50), // 更改为更鲜明的绿色
    secondary = Color(0xFF81C784), // 辅助绿色
    tertiary = Color(0xFF2196F3), // 保留蓝色作为强调色
    background = Color(0xFF121212), // 标准深色背景
    surface = Color(0xFF1E1E1E),   // 卡片等表面颜色
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color(0xFFE0E0E0), // 浅灰色文字
    onSurface = Color(0xFFE0E0E0)    // 表面上的浅灰色文字
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3), // 主色调蓝色
    secondary = Color(0xFF03DAC5), // 辅助色青色
    tertiary = Color(0xFF4CAF50), // 强调色绿色
    background = Color(0xFFF5F5F5), // 背景色
    surface = Color.White, // 表面颜色
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun AIAccountingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // 假设您有一个 Typography.kt 文件定义字体
        content = content
    )
}