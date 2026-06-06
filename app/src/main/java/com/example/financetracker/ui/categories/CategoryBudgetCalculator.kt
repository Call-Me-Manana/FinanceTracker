package com.example.financetracker.ui.categories

import com.example.financetracker.data.Category
import com.example.financetracker.data.TransactionWithCategory
import java.util.Calendar

fun calculateMonthSpent(
    category: Category,
    transactions: List<TransactionWithCategory>
): Double {
    val startOfMonth = getStartOfMonth()

    return transactions
        .filter { item ->
            item.transaction.categoryId == category.id &&
                !item.transaction.isIncome &&
                item.transaction.timestamp >= startOfMonth
        }
        .sumOf { it.transaction.amount }
}

private fun getStartOfMonth(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
