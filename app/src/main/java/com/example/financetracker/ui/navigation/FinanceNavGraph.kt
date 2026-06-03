package com.example.financetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financetracker.CategoryViewModel
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Transaction
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
                },
                navController = navController
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

        composable("edit_transaction/{id}") { backStackEntry ->

            val id = backStackEntry.arguments?.getString("id")?.toInt() ?: return@composable

            EditTransactionRoute(
                transactionId = id,
                viewModel = viewModel,
                categoryViewModel = categoryViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun EditTransactionRoute(
    transactionId: Int,
    viewModel: FinanceViewModel,
    categoryViewModel: CategoryViewModel,
    onBack: () -> Unit
) {
    val transaction = remember { mutableStateOf<Transaction?>(null) }

    LaunchedEffect(transactionId) {
        transaction.value = viewModel.getTransaction(transactionId)
    }

    transaction.value?.let {
        AddTransactionScreen(
            viewModel = viewModel,
            categoryViewModel = categoryViewModel,
            onBack = onBack,
            transactionToEdit = it
        )
    }
}