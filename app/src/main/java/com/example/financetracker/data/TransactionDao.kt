package com.example.financetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.Delete
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: com.example.financetracker.data.Transaction)

    @Delete
    suspend fun delete(transaction: com.example.financetracker.data.Transaction)

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllWithCategory(): Flow<List<TransactionWithCategory>>

    @Update
    suspend fun update(transaction: com.example.financetracker.data.Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Int): com.example.financetracker.data.Transaction
}