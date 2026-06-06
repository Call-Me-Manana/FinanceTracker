package com.example.financetracker.ui.dashboard.model

data class DashboardStats(

    val operationCount: Int,
    val averageDailyExpense: Double,
    val topCategoryName: String?,
    val topCategoryAmount: Double

)