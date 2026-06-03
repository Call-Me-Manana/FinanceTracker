package com.example.financetracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financetracker.data.Category
import com.example.financetracker.data.CategoryDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val dao: CategoryDao
) : ViewModel() {

    val expenseCategories =
        dao.getAll(false)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val incomeCategories =
        dao.getAll(true)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val categories =
        dao.getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addCategory(
        name: String,
        icon: String,
        isIncome: Boolean
    ) {
        viewModelScope.launch {
            dao.insert(
                Category(
                    name = name,
                    icon = icon,
                    isIncome = isIncome
                )
            )
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            dao.delete(category.id)
        }
    }
}