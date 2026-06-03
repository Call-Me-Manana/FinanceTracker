package com.example.financetracker.repository

import com.example.financetracker.data.Category
import com.example.financetracker.data.CategoryDao
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: CategoryDao
) {

    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAll()
    }

    suspend fun insertCategory(
        category: Category
    ) {
        categoryDao.insert(category)
    }
}