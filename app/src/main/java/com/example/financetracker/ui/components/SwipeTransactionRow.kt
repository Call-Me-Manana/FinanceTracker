package com.example.financetracker.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.TransactionWithCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeTransactionRow(
    transaction: TransactionWithCategory,
    viewModel: FinanceViewModel,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false
                }

                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit()
                    false
                }

                SwipeToDismissBoxValue.Settled -> true
            }
        }
    )

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
            viewModel = viewModel,
            onEdit = onEdit
        )
    }
}
