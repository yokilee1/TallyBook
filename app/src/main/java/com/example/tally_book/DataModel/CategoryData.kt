package com.example.tally_book.DataModel

data class CategoryData(
    val category: String,
    val amount: Double,
    val percentage: Float,
    val color: androidx.compose.ui.graphics.Color
)