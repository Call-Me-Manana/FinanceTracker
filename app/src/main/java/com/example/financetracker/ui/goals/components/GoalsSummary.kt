package com.example.financetracker.ui.goals.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.data.Goal

@Composable
fun GoalsSummary(
    goals: List<Goal>,
    modifier: Modifier = Modifier
) {
    val totalTarget = goals.sumOf { it.targetAmount }
    val totalSaved = goals.sumOf { it.currentAmount }
    val progress = (totalSaved / totalTarget).toFloat().coerceIn(0f, 1f)

    Surface(
        modifier = modifier
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
