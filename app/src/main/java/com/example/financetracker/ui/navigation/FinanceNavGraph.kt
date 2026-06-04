package com.example.financetracker.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financetracker.CategoryViewModel
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Transaction
import com.example.financetracker.ui.analytics.AnalyticsScreen
import com.example.financetracker.ui.categories.CategoryScreen
import com.example.financetracker.ui.dashboard.DashboardScreen
import com.example.financetracker.ui.goals.GoalsScreen

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
                categoryViewModel = categoryViewModel,
                navController = navController
            )
        }

        composable(Routes.ANALYTICS) {
            AnalyticsScreen(viewModel)
        }

        composable(Routes.CATEGORIES) {
            CategoryScreen(viewModel)
        }

        composable(Routes.GOALS) {
            GoalsScreen(viewModel)
        }
    }
}
