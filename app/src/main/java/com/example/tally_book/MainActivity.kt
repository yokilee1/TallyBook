package com.example.tally_book

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tally_book.UIComponents.AIAccountingApp
import com.example.tally_book.UIComponents.AIAccountingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIAccountingTheme {
                AIAccountingApp()
            }
        }
    }
}