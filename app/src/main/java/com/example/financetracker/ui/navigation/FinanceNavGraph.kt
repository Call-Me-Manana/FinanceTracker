package com.example.financetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financetracker.CategoryViewModel
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.ui.add.AddTransactionScreen
import com.example.financetracker.ui.analytics.AnalyticsScreen
import com.example.financetracker.ui.categories.CategoryScreen
import com.example.financetracker.ui.dashboard.DashboardScreen

@Composable
fun FinanceNavGraph(navController: NavHostController,
                    viewModel: FinanceViewModel,
                    categoryViewModel: CategoryViewModel,
                    modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD,
        modifier = modifier
    ) {

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                viewModel = viewModel,
                onAddClick = {
                    navController.navigate(Routes.ADD)
                }
            )
        }

        composable(Routes.ADD) {
            AddTransactionScreen(
                viewModel = viewModel,
                categoryViewModel = categoryViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.ANALYTICS) {
            AnalyticsScreen(viewModel)
        }

        composable(Routes.CATEGORIES) {
            CategoryScreen(viewModel)
        }
    }
}