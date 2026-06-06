package com.example.financetracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financetracker.data.Category
import com.example.financetracker.data.Goal
import com.example.financetracker.data.Transaction
import com.example.financetracker.data.TransactionDao
import com.example.financetracker.repository.CategoryRepository
import com.example.financetracker.repository.GoalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceViewModel(
    private val dao: TransactionDao,
    private val goalRepository: GoalRepository,
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

    val goals =
        goalRepository.getAllGoals()
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

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
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

    fun addGoal(title: String, targetAmount: Double, currentAmount: Double) {
        viewModelScope.launch {
            goalRepository.insertGoal(
                Goal(
                    title = title,
                    targetAmount = targetAmount,
                    currentAmount = currentAmount.coerceIn(0.0, targetAmount)
                )
            )
        }
    }

    fun addMoneyToGoal(goal: Goal, amount: Double) {
        if (amount <= 0) return

        viewModelScope.launch {
            goalRepository.addMoney(goal.id, amount)
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            goalRepository.delete(goal)
        }
    }

    fun updateGoal(goal: Goal, title: String, targetAmount: Double, currentAmount: Double) {
        viewModelScope.launch {
            goalRepository.update(
                goal.copy(
                    title = title,
                    targetAmount = targetAmount,
                    currentAmount = currentAmount.coerceIn(0.0, targetAmount)
                )
            )
        }
    }
}
