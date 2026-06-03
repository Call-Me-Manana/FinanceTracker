package com.example.financetracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financetracker.data.TransactionDao
import com.example.financetracker.repository.CategoryRepository

class FinanceViewModelFactory(
    private val dao: TransactionDao,
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            return FinanceViewModel(dao, categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}