package com.example.financetracker.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.collectAsState
import com.example.financetracker.data.Transaction
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.ui.components.TransactionCard
import com.example.financetracker.utils.groupTransactionsByDay
import com.example.financetracker.utils.calculateDayTotal
import java.text.DecimalFormat


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceHomeScreen(
    viewModel: FinanceViewModel,
    onAddClick: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val grouped = groupTransactionsByDay(transactions)
    val format = DecimalFormat("#,###.##")
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance Tracker") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn (
                modifier = Modifier.padding(padding),
                verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
            grouped.forEach { (date, items) ->

                val total = calculateDayTotal(items)

                item {
                    DayCard(
                        date = date,
                        total = total
                    ) {
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

@Composable
fun DayCard(
    date: String,
    total: Double,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column(modifier = Modifier.padding(12.dp)) {


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = if (total >= 0)
                        "+${"%.2f".format(total)} ₽"
                    else
                        "${"%.2f".format(total)} ₽",
                    color = if (total >= 0)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            content()
        }
    }
}