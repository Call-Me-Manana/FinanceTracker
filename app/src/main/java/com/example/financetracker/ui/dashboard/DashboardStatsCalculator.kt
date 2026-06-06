package com.example.financetracker.ui.dashboard

import com.example.financetracker.data.TransactionWithCategory
import com.example.financetracker.ui.dashboard.model.DashboardStats

object DashboardStatsCalculator {
    fun calculate(
        transactions: List<TransactionWithCategory>,
        period: DashboardPeriod
    ): DashboardStats {
        val expenses = transactions.filter { item ->
            !item.transaction.isIncome
        }
        val operationCount = transactions.size
        val totalExpense = expenses.sumOf { item ->
            item.transaction.amount
        }
        val averageDailyExpense = totalExpense / getDaysCount(period)

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

    private fun getDaysCount(period: DashboardPeriod): Int {
        return when (period) {
            DashboardPeriod.TODAY -> 1
            DashboardPeriod.WEEK -> 7
            DashboardPeriod.MONTH -> 30
            DashboardPeriod.ALL -> 30
        }
    }
}