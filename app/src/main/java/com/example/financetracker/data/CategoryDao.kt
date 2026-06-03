package com.example.financetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert
    suspend fun insert(category: Category)

    @Query("""
        SELECT *
        FROM categories
        WHERE isIncome = :isIncome
        ORDER BY name
    """)
    fun getAll(isIncome: Boolean): Flow<List<Category>>

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int
}