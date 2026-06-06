package com.example.financetracker.ui.goals.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.financetracker.data.Goal

@Composable
fun TopUpGoalDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onTopUp: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    val parsedAmount = amount.toDoubleOrNull() ?: 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Пополнить цель") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(goal.title, style = MaterialTheme.typography.titleMedium)
                GoalMoneyTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Сумма"
                )
            }
        },
        confirmButton = {
            Button(
                enabled = parsedAmount > 0.0,
                onClick = { onTopUp(parsedAmount) }
            ) {
                Text("Пополнить")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
