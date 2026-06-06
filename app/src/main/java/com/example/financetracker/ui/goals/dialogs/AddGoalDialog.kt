package com.example.financetracker.ui.goals.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Double, Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var currentAmount by remember { mutableStateOf("") }

    val target = targetAmount.toDoubleOrNull() ?: 0.0
    val current = currentAmount.toDoubleOrNull() ?: 0.0
    val canSave = title.isNotBlank() && target > 0.0 && current >= 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая цель") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    singleLine = true
                )
                GoalMoneyTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it },
                    label = "Нужно накопить"
                )
                GoalMoneyTextField(
                    value = currentAmount,
                    onValueChange = { currentAmount = it },
                    label = "Уже накоплено"
                )
            }
        },
        confirmButton = {
            Button(
                enabled = canSave,
                onClick = { onAdd(title.trim(), target, current) }
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
