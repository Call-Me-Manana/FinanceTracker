package com.example.financetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Transaction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.financetracker.data.TransactionWithCategory
import java.util.Date
import com.example.financetracker.utils.formatDate

@Composable
fun TransactionCard(
    item: TransactionWithCategory,
    viewModel: FinanceViewModel,
    onEdit: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val category = item.category
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (item.transaction.isIncome)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {

        Text(
            text = "${category.icon} ${category.name}",
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEFT
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = if (item.transaction.isIncome)
                        Icons.Default.ArrowUpward
                    else
                        Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (item.transaction.isIncome)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(
                        text = item.transaction.title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = formatDate(item.transaction.timestamp)
                    )

                }
            }

            // RIGHT
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (item.transaction.isIncome)
                        "+${item.transaction.amount} ₽"
                    else
                        "-${item.transaction.amount} ₽",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (item.transaction.isIncome)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )


                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }


    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить запись?") },
            text = { Text("Это действие нельзя отменить") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteTransaction(item.transaction)
                    showDeleteDialog = false
                }) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}