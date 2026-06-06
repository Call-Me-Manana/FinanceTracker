package com.example.financetracker.ui.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Goal
import com.example.financetracker.ui.goals.components.EmptyGoalsState
import com.example.financetracker.ui.goals.components.GoalCard
import com.example.financetracker.ui.goals.components.GoalsSummary
import com.example.financetracker.ui.goals.dialogs.AddGoalDialog
import com.example.financetracker.ui.goals.dialogs.EditGoalDialog
import com.example.financetracker.ui.goals.dialogs.TopUpGoalDialog

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
