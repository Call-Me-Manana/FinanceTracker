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
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.wear.compose.foundation.SwipeToDismissValue
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.TransactionWithCategory
import com.example.financetracker.ui.components.TransactionCard
import com.example.financetracker.utils.groupTransactionsByDay
import com.example.financetracker.utils.calculateDayTotal
import java.text.DecimalFormat
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceHomeScreen(
    viewModel: FinanceViewModel,
    navController: NavController,
    onAddClick: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()

    val grouped = groupTransactionsByDay(transactions)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Finance",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+")
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            grouped.forEach { (date, items) ->

                val total = calculateDayTotal(items)

                item {
                    DayCard(
                        date = date,
                        total = total
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items.forEach { transaction ->
                                SwipeTransactionRow(
                                    transaction = transaction,

                                    onDelete = {
                                        viewModel.deleteTransaction(transaction.transaction)
                                    },

                                    onEdit = {
                                        navController.navigate("edit_transaction/${transaction.transaction.id}")
                                    },
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = if (total >= 0)
                        "+${"%.0f".format(total)} ₽"
                    else
                        "${"%.0f".format(total)} ₽",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (total >= 0)
                        Color(0xFF2ECC71)
                    else
                        MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeTransactionRow(
    transaction: TransactionWithCategory,
    viewModel: FinanceViewModel,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {

    val state = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = state,

        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text("🗑 Delete")
            }
        }
    ) {

        TransactionCard(
            item = transaction,
            viewModel = viewModel
        )
    }


    LaunchedEffect(state.currentValue) {

        when (state.currentValue) {

            SwipeToDismissBoxValue.EndToStart -> {
                onDelete()
            }

            SwipeToDismissBoxValue.StartToEnd -> {
                onEdit()
            }

            else -> {}
        }
    }
}