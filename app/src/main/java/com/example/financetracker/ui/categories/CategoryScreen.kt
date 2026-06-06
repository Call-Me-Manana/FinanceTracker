package com.example.financetracker.ui.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Category
import com.example.financetracker.ui.categories.components.CategoryGrid

@Composable
fun CategoryScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }

    val incomeCategories = remember(categories) {
        categories.filter { it.isIncome }
    }
    val expenseCategories = remember(categories) {
        categories.filter { !it.isIncome }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showDialog = true }
                ) {
                    Text("+")
                }
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text(
                    "Расходы",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                CategoryGrid(
                    categories = expenseCategories,
                    transactions = transactions,
                    onCategoryClick = { categoryToEdit = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Доходы",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                CategoryGrid(
                    categories = incomeCategories,
                    transactions = transactions,
                    onCategoryClick = { categoryToEdit = it }
                )
            }
        }

        if (showDialog) {
            AddCategoryDialog(
                onDismiss = { showDialog = false },
                onSave = { category ->
                    viewModel.addCategory(category)
                    showDialog = false
                }
            )
        }

        categoryToEdit?.let { category ->
            EditCategoryDialog(
                category = category,
                onDismiss = { categoryToEdit = null },
                onSave = { updatedCategory ->
                    viewModel.updateCategory(updatedCategory)
                    categoryToEdit = null
                }
            )
        }
    }
}
