package com.example.financetracker.ui.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Goal

@Composable
fun GoalsScreen(viewModel: FinanceViewModel) {
    val goals by viewModel.goals.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var goalToTopUp by remember { mutableStateOf<Goal?>(null) }
    var goalToEdit by remember { mutableStateOf<Goal?>(null) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Новая цель") }
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            if (goals.isEmpty()) {
                EmptyGoalsState(
                    onAddClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        GoalsSummary(goals = goals)
                    }

                    items(goals, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            onTopUp = { goalToTopUp = goal },
                            onEdit = { goalToEdit = goal },
                            onDelete = { viewModel.deleteGoal(goal) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddGoalDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, targetAmount, currentAmount ->
                viewModel.addGoal(title, targetAmount, currentAmount)
                showAddDialog = false
            }
        )
    }

    goalToTopUp?.let { goal ->
        TopUpGoalDialog(
            goal = goal,
            onDismiss = { goalToTopUp = null },
            onTopUp = { amount ->
                viewModel.addMoneyToGoal(goal, amount)
                goalToTopUp = null
            }
        )
    }

    goalToEdit?.let { goal ->
        EditGoalDialog(
            goal = goal,
            onDismiss = { goalToEdit = null },
            onSave = { title, targetAmount, currentAmount ->
                viewModel.updateGoal(goal, title, targetAmount, currentAmount)
                goalToEdit = null
            }
        )
    }
}

@Composable
private fun GoalsSummary(goals: List<Goal>) {
    val totalTarget = goals.sumOf { it.targetAmount }
    val totalSaved = goals.sumOf { it.currentAmount }
    val progress = (totalSaved / totalTarget).toFloat().coerceIn(0f, 1f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Цели",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = "${"%.0f".format(totalSaved)} из ${"%.0f".format(totalTarget)} ₽",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GoalCard(
    goal: Goal,
    onTopUp: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    val remaining = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Осталось ${"%.0f".format(remaining)} ₽",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${"%.0f".format(goal.currentAmount)} / ${"%.0f".format(goal.targetAmount)} ₽",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = onTopUp, enabled = remaining > 0.0) {
                    Text(if (remaining > 0.0) "Пополнить" else "Готово")
                }
            }
        }
    }
}

@Composable
private fun EditGoalDialog(
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
                MoneyTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it },
                    label = "Нужно накопить"
                )
                MoneyTextField(
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

@Composable
private fun EmptyGoalsState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Flag,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Целей пока нет",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Добавьте первую финансовую цель и отслеживайте прогресс накоплений.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AddGoalDialog(
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
                MoneyTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it },
                    label = "Нужно накопить"
                )
                MoneyTextField(
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

@Composable
private fun TopUpGoalDialog(
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
                MoneyTextField(
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

@Composable
private fun MoneyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            onValueChange(cleanMoneyInput(input))
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}

private fun cleanMoneyInput(input: String): String {
    return buildString {
        var dotAlreadyUsed = false
        input.forEach { char ->
            when {
                char.isDigit() -> append(char)
                char == '.' && !dotAlreadyUsed -> {
                    append(char)
                    dotAlreadyUsed = true
                }
            }
        }
    }
}

private fun Double.toCleanMoneyString(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}
