package com.example.financetracker.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financetracker.CategoryViewModel
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Category
import com.example.financetracker.data.Transaction
import com.example.financetracker.ui.home.DayCard
import com.example.financetracker.ui.home.SwipeTransactionRow
import com.example.financetracker.utils.calculateDayTotal
import com.example.financetracker.utils.groupTransactionsByDay
import kotlinx.coroutines.launch
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    categoryViewModel: CategoryViewModel,
    navController: NavController
) {
    val transactions by viewModel.transactions.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val income by viewModel.totalIncome.collectAsState()
    val expense by viewModel.totalExpense.collectAsState()

    val grouped = groupTransactionsByDay(transactions)

    var showTransactionDialog by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    transactionToEdit = null
                    showTransactionDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {


            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        "Баланс",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )

                    Text(
                        "${"%.2f".format(balance)} ₽",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("↑ ${income.toInt()} ₽")
                        Text("↓ ${expense.toInt()} ₽")
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                grouped.forEach { (date, items) ->
                    val total = calculateDayTotal(items)

                    item {
                        DayCard(date = date, total = total) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items.forEach { transaction ->
                                    SwipeTransactionRow(
                                        transaction = transaction,
                                        viewModel = viewModel,
                                        onDelete = {
                                            viewModel.deleteTransaction(transaction.transaction)
                                        },
                                        onEdit = {
                                            transactionToEdit = transaction.transaction
                                            showTransactionDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (showTransactionDialog) {
                AddTransactionDialog(
                    onDismiss = {
                        showTransactionDialog = false
                        transactionToEdit = null
                    },
                    viewModel = viewModel,
                    categoryViewModel = categoryViewModel,
                    onSaved = {
                        showTransactionDialog = false
                        transactionToEdit = null
                    },
                    transactionToEdit = transactionToEdit
                )
            }
        }
    }
}

@Composable
private fun AddTransactionDialog(
    onDismiss: () -> Unit,
    viewModel: FinanceViewModel,
    categoryViewModel: CategoryViewModel,
    onSaved: () -> Unit,
    transactionToEdit: Transaction? = null
) {
    var title by remember(transactionToEdit?.id) {
        mutableStateOf(transactionToEdit?.title ?: "")
    }
    var amount by remember(transactionToEdit?.id) {
        mutableStateOf(transactionToEdit?.amount?.toCleanMoneyString() ?: "")
    }
    var isIncome by remember(transactionToEdit?.id) {
        mutableStateOf(transactionToEdit?.isIncome ?: true)
    }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var amountError by remember { mutableStateOf(false) }
    val categories by categoryViewModel.categories.collectAsState()
    val parsedAmount = amount.toDoubleOrNull()
    val isValid = title.isNotBlank() && parsedAmount != null && selectedCategory != null

    LaunchedEffect(transactionToEdit?.id, isIncome, categories) {
        val editedCategory = categories.firstOrNull { category ->
            category.id == transactionToEdit?.categoryId && category.isIncome == isIncome
        }
        val fallbackCategory = categories.firstOrNull { category ->
            category.isIncome == isIncome
        }

        selectedCategory = editedCategory ?: fallbackCategory
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("+") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Описание") },
                    singleLine = true
                )
                OutlinedTextField(
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
                Row {
                    FilterChip(
                        selected = isIncome,
                        onClick = { isIncome = true },
                        label = { Text("Доход") },
                    )

                    FilterChip(
                        selected = !isIncome,
                        onClick = { isIncome = false },
                        label = { Text("Расход") },
                    )
                }
                Text(
                    text = "Категория",
                    style = MaterialTheme.typography.titleMedium
                )
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
                        )
                    }
                }

            }
        },
        confirmButton = {
            val scope = rememberCoroutineScope()
            Button(
                enabled = isValid,
                onClick = {
                    scope.launch {
                        val categoryId = selectedCategory?.id ?: return@launch
                        val safeAmount = parsedAmount ?: return@launch

                        if (transactionToEdit == null) {
                            viewModel.addTransaction(
                                title = title,
                                amount = safeAmount,
                                isIncome = isIncome,
                                categoryId = categoryId
                            )
                        } else {
                            viewModel.updateTransaction(
                                transactionToEdit.copy(
                                    title = title,
                                    amount = safeAmount,
                                    isIncome = isIncome,
                                    categoryId = categoryId
                                )
                            )
                        }
                        onSaved()
                    }
                }
            ) {
                Text(if (transactionToEdit == null) "Добавить" else "Сохранить")
            }

        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

private fun Double.toCleanMoneyString(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}
