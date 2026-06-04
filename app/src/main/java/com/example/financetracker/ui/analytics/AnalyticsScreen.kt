package com.example.financetracker.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.utils.expensesByCategory
import androidx.compose.foundation.lazy.items
import androidx.compose.remote.creation.dsl.first
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.financetracker.ui.analytics.components.ExpensesDonutChart
import com.example.financetracker.ui.analytics.components.KpiCard
import com.example.financetracker.ui.analytics.model.AnalyticsPeriod

@Composable
fun AnalyticsScreen(
    viewModel: FinanceViewModel
) {

    val transactions by viewModel.transactions.collectAsState()

    var selectedPeriod by remember {
        mutableStateOf(AnalyticsPeriod.MONTH)
    }

    val now = System.currentTimeMillis()

    val filteredTransactions = remember(
        transactions,
        selectedPeriod
    ) {
        transactions.filter { transaction ->

            val timestamp = transaction.transaction.timestamp

            when (selectedPeriod) {

                AnalyticsPeriod.WEEK ->
                    timestamp >= now - 7L * 24 * 60 * 60 * 1000

                AnalyticsPeriod.MONTH ->
                    timestamp >= now - 30L * 24 * 60 * 60 * 1000

                AnalyticsPeriod.YEAR ->
                    timestamp >= now - 365L * 24 * 60 * 60 * 1000

                AnalyticsPeriod.ALL ->
                    true
            }
        }
    }

    val categoryList = remember(filteredTransactions) {
        expensesByCategory(filteredTransactions)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        // Переключатель периода
        item {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                AnalyticsPeriod.entries.forEach { period ->

                    androidx.compose.material3.FilterChip(
                        selected = period == selectedPeriod,
                        onClick = {
                            selectedPeriod = period
                        },
                        label = {
                            Text(period.title)
                        }
                    )
                }
            }
        }

        // Donut
        item {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                ExpensesDonutChart(
                    data = categoryList
                )
            }
        }


    }
}