package com.example.financetracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financetracker.data.GoalDao
import com.example.financetracker.data.TransactionDao
import com.example.financetracker.repository.CategoryRepository
import com.example.financetracker.repository.GoalRepository

class FinanceViewModelFactory(
    private val dao: TransactionDao,
    private val goalRepository: GoalRepository,
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            return FinanceViewModel(dao, goalRepository, categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
