package com.example.financetracker.ui.dashboard

import com.example.financetracker.data.TransactionWithCategory
import com.example.financetracker.ui.dashboard.model.DashboardPeriod
import com.example.financetracker.utils.getStartOfDay
import java.util.Calendar

fun filterTransactionsByPeriod(
    transactions: List<TransactionWithCategory>,
    period: DashboardPeriod
): List<TransactionWithCategory> {
    val now = System.currentTimeMillis()
    val startTime = when (period) {
        DashboardPeriod.TODAY -> getStartOfDay(now)
        DashboardPeriod.WEEK -> getStartOfWeek(now)
        DashboardPeriod.MONTH -> getStartOfMonth(now)
        DashboardPeriod.ALL -> Long.MIN_VALUE
    }

    return transactions.filter { item ->
        item.transaction.timestamp >= startTime
    }
}

fun filterTransactionsBySearch(
    transactions: List<TransactionWithCategory>,
    query: String
): List<TransactionWithCategory> {
    val trimmedQuery = query.trim()

    if (trimmedQuery.isBlank()) {
        return transactions
    }

    return transactions.filter { item ->
        item.transaction.title.contains(
            other = trimmedQuery,
            ignoreCase = true
        ) || item.category.name.contains(
            other = trimmedQuery,
            ignoreCase = true
        )
    }
}

private fun getStartOfWeek(time: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

private fun getStartOfMonth(time: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}
