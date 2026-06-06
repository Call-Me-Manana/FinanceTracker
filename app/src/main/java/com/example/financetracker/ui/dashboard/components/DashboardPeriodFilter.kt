package com.example.financetracker.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.ui.dashboard.model.DashboardPeriod

@Composable
fun DashboardPeriodFilter(
    selectedPeriod: DashboardPeriod,
    onPeriodSelected: (DashboardPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DashboardPeriod.entries.forEach { period ->
            FilterChip(
                modifier = Modifier.weight(1f),
                selected = selectedPeriod == period,
                onClick = {
                    onPeriodSelected(period)
                },
                label = {
                    Text(period.title)
                }
            )
        }
    }
}
