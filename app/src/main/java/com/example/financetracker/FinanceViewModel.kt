package com.example.financetracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financetracker.data.Category
import com.example.financetracker.data.Transaction
import com.example.financetracker.data.TransactionDao
import com.example.financetracker.repository.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceViewModel(
    private val dao: TransactionDao,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    val categories =
        categoryRepository
            .getAllCategories()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    val transactions =
        dao.getAllWithCategory()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    suspend fun addTransaction(
        title: String,
        amount: Double,
        isIncome: Boolean,
        categoryId: Int
    ) {
        require(categoryId > 0) {
            "Category must be selected"
        }
        viewModelScope.launch {
            dao.insert(
                Transaction(
                    title = title,
                    amount = amount,
                    isIncome = isIncome,
                    categoryId = categoryId
                )
            )
        }
    }
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.delete(transaction)
        }
    }

    val balance = transactions.map { list ->
        list.sumOf { if (it.transaction.isIncome) it.transaction.amount else -it.transaction.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalIncome = transactions.map { list ->
        list.filter { it.transaction.isIncome }.sumOf { it.transaction.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense = transactions.map { list ->
        list.filter { !it.transaction.isIncome }.sumOf { it.transaction.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addCategory(
        category: Category
    ) {
        viewModelScope.launch {
            categoryRepository.insertCategory(category)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.update(transaction)
        }
    }

    suspend fun getTransaction(id: Int): Transaction {
        return dao.getById(id)
    }
}