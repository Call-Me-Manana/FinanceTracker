package com.example.financetracker

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financetracker.data.TransactionDao
import com.example.financetracker.ui.add.AddTransactionScreen
import com.example.financetracker.ui.home.FinanceHomeScreen

@Composable
fun FinanceAppNavigation(viewModel: FinanceViewModel, categoryViewModel: CategoryViewModel) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            FinanceHomeScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate("add") }
            )
        }

        composable("add") {
            AddTransactionScreen(
                viewModel = viewModel,
                categoryViewModel = categoryViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}