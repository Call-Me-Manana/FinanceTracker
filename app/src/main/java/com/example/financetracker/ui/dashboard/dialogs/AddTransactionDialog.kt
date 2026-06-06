package com.example.financetracker.ui.dashboard.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.example.financetracker.CategoryViewModel
import com.example.financetracker.FinanceViewModel
import com.example.financetracker.data.Category
import com.example.financetracker.data.Transaction
import com.example.financetracker.utils.cleanMoneyInput
import com.example.financetracker.utils.toCleanMoneyString
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
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
    var selectedDateMillis by remember(transactionToEdit?.id) {
        mutableStateOf(transactionToEdit?.timestamp ?: System.currentTimeMillis())
    }
    var showDatePicker by remember { mutableStateOf(false) }
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
                        amount = cleanMoneyInput(input)
                        if (amountError) amountError = false
                    },
                    label = { Text("Сумма") },
                    isError = amountError,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        showDatePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Дата: ${formatTransactionDate(selectedDateMillis)}")
                }

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
                                categoryId = categoryId,
                                timestamp = selectedDateMillis
                            )
                        } else {
                            viewModel.updateTransaction(
                                transactionToEdit.copy(
                                    title = title,
                                    amount = safeAmount,
                                    isIncome = isIncome,
                                    categoryId = categoryId,
                                    timestamp = selectedDateMillis
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

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDateMillis =
                            datePickerState.selectedDateMillis ?: selectedDateMillis
                        showDatePicker = false
                    }
                ) {
                    Text("Выбрать")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatTransactionDate(timestamp: Long): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(timestamp))
}
