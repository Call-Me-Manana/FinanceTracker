package com.example.financetracker.utils

import com.example.financetracker.data.Transaction
import com.example.financetracker.data.TransactionWithCategory
import java.text.SimpleDateFormat
import java.util.*
import com.example.financetracker.utils.getStartOfDay

fun groupTransactionsByDay(list: List<TransactionWithCategory>): Map<String, List<TransactionWithCategory>> {
    return list.groupBy { item ->
        formatGroupDate(item.transaction.timestamp)
    }
}

private fun formatGroupDate(timestamp: Long): String {
    val now = System.currentTimeMillis()

    val startOfToday = getStartOfDay(now)
    val startOfYesterday = startOfToday - 24 * 60 * 60 * 1000

    return when {
        timestamp >= startOfToday -> "Сегодня"
        timestamp >= startOfYesterday -> "Вчера"
        else -> {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

fun calculateDayTotal(transactions: List<TransactionWithCategory>): Double {
    return transactions.sumOf { item ->
        if (item.transaction.isIncome) item.transaction.amount else -item.transaction.amount
    }
}