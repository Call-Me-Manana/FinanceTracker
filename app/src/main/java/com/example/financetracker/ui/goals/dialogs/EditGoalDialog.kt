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
import com.example.financetracker.data.Goal
import com.example.financetracker.utils.toCleanMoneyString

@Composable
fun EditGoalDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onSave: (String, Double, Double) -> Unit
) {
    var title by remember(goal.id) { mutableStateOf(goal.title) }
    var targetAmount by remember(goal.id) { mutableStateOf(goal.targetAmount.toCleanMoneyString()) }
    var currentAmount by remember(goal.id) { mutableStateOf(goal.currentAmount.toCleanMoneyString()) }

    val target = targetAmount.toDoubleOrNull() ?: 0.0
    val current = currentAmount.toDoubleOrNull() ?: 0.0
    val canSave = title.isNotBlank() && target > 0.0 && current >= 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать цель") },
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
                onClick = { onSave(title.trim(), target, current) }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
