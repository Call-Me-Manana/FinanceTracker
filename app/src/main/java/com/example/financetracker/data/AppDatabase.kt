package com.example.financetracker.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Transaction::class, Category::class, Goal::class],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao
}
