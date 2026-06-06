package com.example.financetracker.ui.categories.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.financetracker.data.Category

@Composable
fun CategoryGridItem(
    category: Category,
    monthSpent: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val budget = category.monthlyBudget
    val progress = if (budget != null && budget > 0.0) {
        (monthSpent / budget).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = category.icon,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (!category.isIncome) {
            Spacer(modifier = Modifier.height(6.dp))

            if (budget != null && budget > 0.0) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${monthSpent.toInt()} / ${budget.toInt()} ₽",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (monthSpent > budget) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            } else {
                Text(
                    text = "Без бюджета",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
