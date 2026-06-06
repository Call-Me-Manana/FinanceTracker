package com.example.financetracker.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Category
import com.example.financetracker.data.TransactionWithCategory
import java.util.Calendar

@Composable
fun CategoryScreen(
    viewModel: FinanceViewModel
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
                .padding(
                    padding
                )
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
                viewModel.updateCategory(
                    updatedCategory
                )
                categoryToEdit = null
            }
        )
    }
}

@Composable
fun CategoryGrid(
    categories: List<Category>,
    transactions: List<TransactionWithCategory>,
    onCategoryClick: (Category) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 96.dp),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.heightIn(max = 400.dp)
    ) {
        items(categories) { category ->
            val monthSpent = remember(transactions, category.id) {
                calculateMonthSpent(
                    category = category,
                    transactions = transactions
                )
            }

            CategoryGridItem(
                category = category,
                monthSpent = monthSpent,
                onClick = {
                    onCategoryClick(category)
                }
            )
        }
    }
}

@Composable
fun CategoryGridItem(
    category: Category,
    monthSpent: Double,
    onClick: () -> Unit
) {
    val budget = category.monthlyBudget
    val progress = if (budget != null && budget > 0.0) {
        (monthSpent / budget).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = category.icon,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (!category.isIncome) {
            Spacer(modifier = Modifier.height(6.dp))

            if (budget != null && budget > 0.0) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${monthSpent.toInt()} / ${budget.toInt()} ₽",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (monthSpent > budget) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            } else {
                Text(
                    text = "Без бюджета",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun calculateMonthSpent(
    category: Category,
    transactions: List<TransactionWithCategory>
): Double {
    val startOfMonth = getStartOfMonth()

    return transactions
        .filter { item ->
            item.transaction.categoryId == category.id &&
                    !item.transaction.isIncome &&
                    item.transaction.timestamp >= startOfMonth
        }
        .sumOf { it.transaction.amount }
}

private fun getStartOfMonth(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}



