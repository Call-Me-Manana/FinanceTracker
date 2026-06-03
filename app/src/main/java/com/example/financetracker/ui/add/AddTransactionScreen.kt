package com.example.financetracker.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Transaction
import com.example.financetracker.CategoryViewModel
import com.example.financetracker.data.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: FinanceViewModel,
    categoryViewModel: CategoryViewModel,
    onBack: () -> Unit,
    transactionToEdit: Transaction? = null
) {
    var title by remember { mutableStateOf(transactionToEdit?.title ?: "") }
    var amount by remember { mutableStateOf(transactionToEdit?.amount?.toString() ?: "") }
    var isIncome by remember { mutableStateOf(transactionToEdit?.isIncome ?: true) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    val isValid = title.isNotBlank()
    var amountError by remember { mutableStateOf(false) }
    val categories by categoryViewModel.categories.collectAsState()

    LaunchedEffect(isIncome, categories) {

        if (
            selectedCategory == null ||
            selectedCategory?.isIncome != isIncome
        ) {

            selectedCategory = categories.firstOrNull()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (transactionToEdit == null) "Добавить" else "Сохранить") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = amount,
                        onValueChange = { input ->
                            val cleaned = buildString {
                                var dotAlreadyUsed = false
                                for (char in input) {
                                    when {
                                        char.isDigit() -> append(char)
                                        char == '.' && !dotAlreadyUsed -> {
                                            append(char)
                                            dotAlreadyUsed = true
                                        }
                                    }
                                }
                            }
                            amount = cleaned
                            if (amountError) amountError = false
                        },
                        label = { Text("Сумма") },
                        isError = amountError,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        FilterChip(
                            selected = isIncome,
                            onClick = { isIncome = true },
                            label = { Text("Доход") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isIncome == true,
                                borderColor = MaterialTheme.colorScheme.outline,
                                selectedBorderColor = MaterialTheme.colorScheme.primary,
                                borderWidth = 1.dp,
                                selectedBorderWidth = 2.dp
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FilterChip(
                            selected = !isIncome,
                            onClick = { isIncome = false },
                            label = { Text("Расход") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isIncome == false,
                                borderColor = MaterialTheme.colorScheme.outline,
                                selectedBorderColor = MaterialTheme.colorScheme.primary,
                                borderWidth = 1.dp,
                                selectedBorderWidth = 2.dp
                            )
                        )
                    }

                    Text(
                        text = "Категория",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    val filteredCategories = remember(categories, isIncome) {
                        categories.filter { it.isIncome == isIncome }
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filteredCategories.forEach { category ->

                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = {
                                    selectedCategory = category
                                },
                                label = {
                                    Text("${category.icon} ${category.name}")
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedCategory == category,
                                    borderColor = MaterialTheme.colorScheme.outline,
                                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 2.dp
                                )
                            )
                        }
                    }

                    Button(
                        enabled = isValid,
                        onClick = {
                            if (transactionToEdit == null) {
                                viewModel.addTransaction(
                                    title = title,
                                    amount = amount.toDoubleOrNull() ?: 0.0,
                                    isIncome = isIncome,
                                    categoryId = selectedCategory?.id!!
                                )
                                onBack()
                            } else {
                                viewModel.updateTransaction(
                                    transactionToEdit.copy(
                                        title = title,
                                        amount = amount.toDoubleOrNull() ?: 0.0,
                                        isIncome = isIncome,
                                        categoryId = selectedCategory?.id!!
                                    )
                                )
                            }
                            onBack()
                        }

                    ) {
                        Text(if (transactionToEdit == null) "Добавить" else "Сохранить")
                    }
                }
            }
        }
    }

}