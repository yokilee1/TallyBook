package com.example.tally_book.UIComponents

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AIAccountingApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(navController)
        }
        composable("add_transaction") {
            AddTransactionScreen(navController)
        }
        composable("stats") {
            StatsScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
        composable("history") {
            HistoryScreen(navController)
        }
    }
}
