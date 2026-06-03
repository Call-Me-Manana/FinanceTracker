package com.example.financetracker.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Category

@Composable
fun CategoryScreen(
    viewModel: FinanceViewModel
) {
    val categories by viewModel.categories.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    val incomeCategories = remember(categories) {
        categories.filter { it.isIncome }
    }

    val expenseCategories = remember(categories) {
        categories.filter { !it.isIncome }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Text("+")
            }
        }
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

            CategoryGrid(expenseCategories)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Доходы",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            CategoryGrid(incomeCategories)
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
}

@Composable
fun CategoryGrid(
    categories: List<Category>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.heightIn(max = 400.dp)
    ) {
        items(categories) { category ->
            CategoryGridItem(category)
        }
    }
}

@Composable
fun CategoryGridItem(
    category: Category
) {
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .clickable { },
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
            style = MaterialTheme.typography.labelSmall
        )
    }
}