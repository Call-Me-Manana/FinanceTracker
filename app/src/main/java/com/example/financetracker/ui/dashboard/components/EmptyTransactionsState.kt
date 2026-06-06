package com.example.financetracker.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.ui.dashboard.model.DashboardPeriod

@Composable
fun EmptyTransactionsState(
    hasTransactions: Boolean,
    searchQuery: String,
    selectedPeriod: DashboardPeriod,
    modifier: Modifier = Modifier
) {
    val title = when {
        !hasTransactions -> "Транзакций пока нет"
        searchQuery.isNotBlank() -> "Ничего не найдено"
        else -> "Нет транзакций за период"
    }

    val message = when {
        !hasTransactions -> "Добавьте первую операцию через кнопку +."
        searchQuery.isNotBlank() -> "Попробуйте изменить текст поиска или выбрать другой период."
        else -> "За период «${selectedPeriod.title}» операций не найдено."
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
