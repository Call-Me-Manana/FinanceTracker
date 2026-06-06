package com.example.financetracker.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Transaction
import com.example.financetracker.data.TransactionWithCategory
import com.example.financetracker.ui.components.DayCard
import com.example.financetracker.ui.components.SwipeTransactionRow
import com.example.financetracker.utils.calculateDayTotal

@Composable
fun DashboardTransactionList(
    groupedTransactions: Map<String, List<TransactionWithCategory>>,
    viewModel: FinanceViewModel,
    onEdit: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        groupedTransactions.forEach { (date, items) ->
            val total = calculateDayTotal(items)

            item {
                DayCard(date = date, total = total) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items.forEach { transaction ->
                            SwipeTransactionRow(
                                transaction = transaction,
                                viewModel = viewModel,
                                onDelete = {
                                    viewModel.deleteTransaction(transaction.transaction)
                                },
                                onEdit = {
                                    onEdit(transaction.transaction)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
