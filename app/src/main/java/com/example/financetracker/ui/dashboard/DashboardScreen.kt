package com.example.financetracker.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financetracker.CategoryViewModel
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Category
import com.example.financetracker.data.Transaction
import com.example.financetracker.data.TransactionWithCategory
import com.example.financetracker.ui.dashboard.model.DashboardStats
import com.example.financetracker.ui.home.DayCard
import com.example.financetracker.ui.home.SwipeTransactionRow
import com.example.financetracker.utils.calculateDayTotal
import com.example.financetracker.utils.getStartOfDay
import com.example.financetracker.utils.groupTransactionsByDay
import com.example.financetracker.utils.toCleanMoneyString
import kotlinx.coroutines.launch

enum class DashboardPeriod(
    val title: String
) {
    TODAY("Сегодня"),
    WEEK("Неделя"),
    MONTH("Месяц"),
    ALL("Все")
}

@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    categoryViewModel: CategoryViewModel,
    navController: NavController
) {
    val transactions by viewModel.transactions.collectAsState()

    var selectedPeriod by remember { mutableStateOf(DashboardPeriod.MONTH) }

    val periodTransactions = remember(transactions, selectedPeriod) {
        filterTransactionsByPeriod(
            transactions = transactions,
            period = selectedPeriod
        )
    }
    var searchQuery by remember { mutableStateOf("") }
    val filteredTransactions = remember(periodTransactions, searchQuery) {
        filterTransactionsBySearch(
            transactions = periodTransactions,
            query = searchQuery
        )
    }

    val grouped = groupTransactionsByDay(filteredTransactions)

    val periodBalance = remember(filteredTransactions) {
        filteredTransactions.sumOf { item ->
            if (item.transaction.isIncome) {
                item.transaction.amount
            } else {
                -item.transaction.amount
            }
        }
    }

    val dashboardStats = remember(filteredTransactions, selectedPeriod) {
        DashboardStatsCalculator.calculate(
            transactions = filteredTransactions,
            period = selectedPeriod
        )
    }

    val periodIncome = remember(filteredTransactions) {
        filteredTransactions
            .filter { it.transaction.isIncome }
            .sumOf { it.transaction.amount }
    }

    val periodExpense = remember(filteredTransactions) {
        filteredTransactions
            .filter { !it.transaction.isIncome }
            .sumOf { it.transaction.amount }
    }

    var showTransactionDialog by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }



    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                    .padding(
                        16.dp
                    ),
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
                        "${"%.2f".format(periodBalance)} ₽",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("↑ ${periodIncome.toInt()} ₽")
                        Text("↓ ${periodExpense.toInt()} ₽")
                    }
                }
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DashboardPeriod.entries.forEach { period ->
                    FilterChip(
                        modifier = Modifier.weight(1f),
                        selected = selectedPeriod == period,
                        onClick = {
                            selectedPeriod = period
                        },
                        label = {
                            Text(period.title)
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                },
                label = {
                    Text("Поиск")
                },
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge

            )

            Spacer(Modifier.height(16.dp))

            DashboardStatsRow(
                stats = dashboardStats
            )

            Spacer(Modifier.height(12.dp))

            if (filteredTransactions.isEmpty()) {
                EmptyTransactionsState(
                    hasTransactions = transactions.isNotEmpty(),
                    searchQuery = searchQuery,
                    selectedPeriod = selectedPeriod
                )
            } else {
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

private fun filterTransactionsByPeriod(
    transactions: List<TransactionWithCategory>,
    period: DashboardPeriod
): List<TransactionWithCategory> {
    val now = System.currentTimeMillis()

    val startTime = when (period) {
        DashboardPeriod.TODAY -> getStartOfDay(now)
        DashboardPeriod.WEEK -> getStartOfWeek(now)
        DashboardPeriod.MONTH -> getStartOfMonth(now)
        DashboardPeriod.ALL -> Long.MIN_VALUE
    }

    return transactions.filter { item ->
        item.transaction.timestamp >= startTime
    }
}

private fun getStartOfWeek(time: Long): Long {
    val calendar = java.util.Calendar.getInstance()
    calendar.timeInMillis = time
    calendar.firstDayOfWeek = java.util.Calendar.MONDAY
    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
    calendar.set(java.util.Calendar.MINUTE, 0)
    calendar.set(java.util.Calendar.SECOND, 0)
    calendar.set(java.util.Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

private fun getStartOfMonth(time: Long): Long {
    val calendar = java.util.Calendar.getInstance()
    calendar.timeInMillis = time
    calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
    calendar.set(java.util.Calendar.MINUTE, 0)
    calendar.set(java.util.Calendar.SECOND, 0)
    calendar.set(java.util.Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

private fun filterTransactionsBySearch(
    transactions: List<TransactionWithCategory>,
    query: String
): List<TransactionWithCategory> {
    val trimmedQuery = query.trim()

    if (trimmedQuery.isBlank()) {
        return transactions
    }

    return transactions.filter { item ->
        item.transaction.title.contains(
            other = trimmedQuery,
            ignoreCase = true
        ) || item.category.name.contains(
            other = trimmedQuery,
            ignoreCase = true
        )
    }
}

@Composable
private fun EmptyTransactionsState(
    hasTransactions: Boolean,
    searchQuery: String,
    selectedPeriod: DashboardPeriod
) {
    val title = when {
        !hasTransactions -> "Транзакций пока нет"
        searchQuery.isNotBlank() -> "Ничего не найдено"
        else -> "Нет транзакций за период"
    }

    val message = when {
        !hasTransactions -> "Добавьте первую операцию через кнопку +."
        searchQuery.isNotBlank() -> "Попробуйте изменить текст поиска или выбрать другой период."
        else -> "За период «${selectedPeriod.title}» операций не найдено."
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DashboardStatsRow(
    stats: DashboardStats
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard("Операций", stats.operationCount.toString(), modifier = Modifier.weight(1f))
        StatCard(
            title = "В день",
            value = "${stats.averageDailyExpense.toInt()} ₽",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Топ",
            value = stats.topCategoryName ?: "Нет",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,

        ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),

            ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
