package com.example.financetracker.utils

import com.example.financetracker.data.TransactionWithCategory
import com.example.financetracker.data.model.CategoryExpense

fun expensesByCategory(
    transactions: List<TransactionWithCategory>
): List<CategoryExpense>{

    return transactions
        .filter { !it.transaction.isIncome }
        .groupBy { it.category.name }
        .map { (category, items) ->
            CategoryExpense(
                category = category,
                amount = items.sumOf { it.transaction.amount }
            )
        }
}