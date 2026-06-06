package com.example.financetracker.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.ui.dashboard.model.DashboardDateRange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardDateRangeFilter(
    dateRange: DashboardDateRange,
    onDateRangeChange: (DashboardDateRange) -> Unit,
    modifier: Modifier = Modifier
) {
    var pickingStart by remember { mutableStateOf<Boolean?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DateRangeField(
                modifier = Modifier.weight(1f),
                title = "Начало",
                value = formatDateOrAny(dateRange.startDateMillis),
                onClick = { pickingStart = true }
            )

            DateRangeField(
                modifier = Modifier.weight(1f),
                title = "Конец",
                value = formatDateOrAny(dateRange.endDateMillis),
                onClick = { pickingStart = false }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            FilterChip(
                modifier = Modifier.weight(1f),
                selected = isSameDateRange(dateRange, 1),
                onClick = {
                    onDateRangeChange(createDateRangeForLastDays(1))
                },
                label = { Text("Сегодня") }
            )

            FilterChip(
                modifier = Modifier.weight(1f),
                selected = isSameDateRange(dateRange, 7),
                onClick = {
                    onDateRangeChange(createDateRangeForLastDays(7))
                },
                label = { Text("Неделя") }
            )

            FilterChip(
                modifier = Modifier.weight(1f),
                selected = isSameDateRange(dateRange, 30),
                onClick = {
                    onDateRangeChange(createDateRangeForLastDays(30))
                },
                label = { Text("Месяц") }
            )

            FilterChip(
                modifier = Modifier.weight(1f),
                selected = isAllRange(dateRange),
                onClick = {
                    onDateRangeChange(DashboardDateRange())
                },
                label = { Text("Все") }
            )
        }
        pickingStart?.let { isStart ->

            val initialDate = if (isStart) {
                dateRange.startDateMillis
            } else {
                dateRange.endDateMillis
            }

            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = initialDate
            )

            DatePickerDialog(
                onDismissRequest = {
                    pickingStart = null
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedDate = datePickerState.selectedDateMillis

                            if (selectedDate != null) {
                                if (isStart) {
                                    onDateRangeChange(
                                        dateRange.copy(startDateMillis = selectedDate)
                                    )
                                } else {
                                    onDateRangeChange(
                                        dateRange.copy(endDateMillis = selectedDate)
                                    )
                                }
                            }

                            pickingStart = null
                        }
                    ) {
                        Text("Выбрать")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            pickingStart = null
                        }
                    ) {
                        Text("Отмена")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun DateRangeField(
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun formatDateOrAny(timestamp: Long?): String {
    return timestamp?.let { millis ->
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(millis))
    } ?: "любая"
}

private fun createDateRangeForLastDays(days: Int): DashboardDateRange {
    val endCalendar = Calendar.getInstance()

    val startCalendar = Calendar.getInstance()
    startCalendar.add(Calendar.DAY_OF_YEAR, -(days - 1))

    return DashboardDateRange(
        startDateMillis = startCalendar.timeInMillis,
        endDateMillis = endCalendar.timeInMillis
    )
}

private fun isSameDateRange(
    currentRange: DashboardDateRange,
    days: Int
): Boolean {
    val quickRange = createDateRangeForLastDays(days)

    return isSameDay(currentRange.startDateMillis, quickRange.startDateMillis) &&
            isSameDay(currentRange.endDateMillis, quickRange.endDateMillis)
}

private fun isSameDay(
    firstMillis: Long?,
    secondMillis: Long?
): Boolean {
    if (firstMillis == null || secondMillis == null) {
        return false
    }

    val firstCalendar = Calendar.getInstance().apply {
        timeInMillis = firstMillis
    }

    val secondCalendar = Calendar.getInstance().apply {
        timeInMillis = secondMillis
    }

    return firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR) &&
            firstCalendar.get(Calendar.DAY_OF_YEAR) == secondCalendar.get(Calendar.DAY_OF_YEAR)
}

private fun isAllRange(dateRange: DashboardDateRange): Boolean {
    return dateRange.startDateMillis == null && dateRange.endDateMillis == null
}