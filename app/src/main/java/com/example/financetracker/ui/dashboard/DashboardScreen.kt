package com.example.financetracker.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financetracker.CategoryViewModel
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Transaction
import com.example.financetracker.ui.dashboard.components.BalanceSummaryCard
import com.example.financetracker.ui.dashboard.components.DashboardSearchField
import com.example.financetracker.ui.dashboard.components.DashboardStatsRow
import com.example.financetracker.ui.dashboard.components.DashboardTransactionList
import com.example.financetracker.ui.dashboard.components.EmptyTransactionsState
import com.example.financetracker.ui.dashboard.dialogs.AddTransactionDialog
import com.example.financetracker.ui.dashboard.model.DashboardDateRange
import com.example.financetracker.utils.groupTransactionsByDay

@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    categoryViewModel: CategoryViewModel,
    navController: NavController
) {
    val transactions by viewModel.transactions.collectAsState()

    var selectedDateRange by remember {
        mutableStateOf(DashboardDateRange())
    }
    var searchQuery by remember { mutableStateOf("") }
    var showTransactionDialog by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }

    val dateRangeTransactions = remember(transactions, selectedDateRange) {
        filterTransactionsByDateRange(
            transactions = transactions,
            dateRange = selectedDateRange
        )
    }
    val filteredTransactions = remember(dateRangeTransactions, searchQuery) {
        filterTransactionsBySearch(
            transactions = dateRangeTransactions,
            query = searchQuery
        )
    }
    val grouped = remember(filteredTransactions) {
        groupTransactionsByDay(filteredTransactions)
    }

    val periodBalance = remember(filteredTransactions) {
        filteredTransactions.sumOf { item ->
            if (item.transaction.isIncome) {
                item.transaction.amount
            } else {
                -item.transaction.amount
            }
        }
    }
    val periodIncome = remember(filteredTransactions) {
        filteredTransactions
            .filter { it.transaction.isIncome }
            .sumOf { it.transaction.amount }
    }
    val periodExpense = remember(filteredTransactions) {
        filteredTransactions
            .filter { !it.transaction.isIncome }
            .sumOf { it.transaction.amount }
    }
    val dashboardStats = remember(filteredTransactions, selectedDateRange) {
        DashboardStatsCalculator.calculate(
            transactions = filteredTransactions,
            dateRange = selectedDateRange
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    transactionToEdit = null
                    showTransactionDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            BalanceSummaryCard(
                balance = periodBalance,
                income = periodIncome,
                expense = periodExpense
            )

            DashboardDateRangeFilter(
                dateRange = selectedDateRange,
                onDateRangeChange = { newRange ->
                    selectedDateRange = newRange
                }
            )

            Spacer(Modifier.height(8.dp))

            DashboardSearchField(
                query = searchQuery,
                onQueryChange = { query ->
                    searchQuery = query
                }
            )

            Spacer(Modifier.height(16.dp))

            DashboardStatsRow(stats = dashboardStats)

            Spacer(Modifier.height(12.dp))

            if (filteredTransactions.isEmpty()) {
                EmptyTransactionsState(
                    hasTransactions = transactions.isNotEmpty(),
                    searchQuery = searchQuery,
                    dateRange = selectedDateRange
                )
            } else {
                DashboardTransactionList(
                    groupedTransactions = grouped,
                    viewModel = viewModel,
                    onEdit = { transaction ->
                        transactionToEdit = transaction
                        showTransactionDialog = true
                    }
                )
            }

            if (showTransactionDialog) {
                AddTransactionDialog(
                    onDismiss = {
                        showTransactionDialog = false
                        transactionToEdit = null
                    },
                    viewModel = viewModel,
                    categoryViewModel = categoryViewModel,
                    onSaved = {
                        showTransactionDialog = false
                        transactionToEdit = null
                    },
                    transactionToEdit = transactionToEdit
                )
            }
        }
    }
}
