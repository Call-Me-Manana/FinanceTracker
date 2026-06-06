package com.example.financetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.financetracker.repository.CategoryRepository
import com.example.financetracker.repository.GoalRepository


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = DatabaseProvider.getDatabase(this)
        val categoryRepository = CategoryRepository(
            database.categoryDao()
        )
        val goalRepository = GoalRepository(
            database.goalDao()
        )
        val viewModel = FinanceViewModel(
            database.transactionDao(),
            goalRepository,
            categoryRepository
        )

        val categoryViewModel = CategoryViewModel(
            database.categoryDao()
        )


        setContent {
            FinanceTrackerApp(
                viewModel = viewModel,
                categoryViewModel = categoryViewModel
            )
        }
    }
}
