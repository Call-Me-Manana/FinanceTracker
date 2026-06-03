package com.example.financetracker.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.ui.components.TransactionCard
import com.example.financetracker.ui.home.DayCard
import com.example.financetracker.utils.calculateDayTotal
import com.example.financetracker.utils.groupTransactionsByDay
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    onAddClick: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val income by viewModel.totalIncome.collectAsState()
    val expense by viewModel.totalExpense.collectAsState()

    val grouped = groupTransactionsByDay(transactions)

    Column(modifier = Modifier.fillMaxSize()) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("Баланс", style = MaterialTheme.typography.titleMedium)

                Text(
                    text = "${"%.2f".format(balance)} ₽",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Доход: +${"%.2f".format(income)} ₽")
                    Text("Расход: -${"%.2f".format(expense)} ₽")
                }
            }
        }

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            grouped.forEach { (date, items) ->
                val total = calculateDayTotal(items)

                item {
                    DayCard(date = date, total = total) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items.forEach { transaction ->
                                TransactionCard(
                                    item = transaction,
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}