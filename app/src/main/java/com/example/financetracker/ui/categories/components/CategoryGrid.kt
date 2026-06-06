package com.example.financetracker.ui.categories.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.data.Category
import com.example.financetracker.data.TransactionWithCategory
import com.example.financetracker.ui.categories.calculateMonthSpent

@Composable
fun CategoryGrid(
    categories: List<Category>,
    transactions: List<TransactionWithCategory>,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 96.dp),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.heightIn(max = 400.dp)
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
