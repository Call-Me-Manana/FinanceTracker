package com.example.financetracker.ui.dashboard

import com.example.financetracker.data.TransactionWithCategory
import com.example.financetracker.ui.dashboard.model.DashboardDateRange
import com.example.financetracker.ui.dashboard.model.DashboardStats

private const val DAY_IN_MILLIS = 24 * 60 * 60 * 1000L

object DashboardStatsCalculator {
    fun calculate(
        transactions: List<TransactionWithCategory>,
        dateRange: DashboardDateRange
    ): DashboardStats {
        val expenses = transactions.filter { item ->
            !item.transaction.isIncome
        }
        val operationCount = transactions.size
        val totalExpense = expenses.sumOf { item ->
            item.transaction.amount
        }
        val averageDailyExpense = totalExpense / getDaysCount(dateRange)

        val topCategory = expenses
            .groupBy { item -> item.category.name }
            .mapValues { entry ->
                entry.value.sumOf { item -> item.transaction.amount }
            }
            .maxByOrNull { entry -> entry.value }

        return DashboardStats(
            operationCount = operationCount,
            averageDailyExpense = averageDailyExpense,
            topCategoryName = topCategory?.key,
            topCategoryAmount = topCategory?.value ?: 0.0
        )
    }

    private fun getDaysCount(dateRange: DashboardDateRange): Int {
        val startDate = dateRange.startDateMillis
        val endDate = dateRange.endDateMillis

        if (startDate == null || endDate == null) {
            return 30
        }

        val difference = endDate - startDate
        val days = difference / DAY_IN_MILLIS + 1

        return days.toInt().coerceAtLeast(1)
    }
}
