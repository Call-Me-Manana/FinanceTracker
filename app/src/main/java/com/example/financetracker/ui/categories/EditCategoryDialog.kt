package com.example.financetracker.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.data.Category
import com.example.financetracker.utils.cleanMoneyInput
import com.example.financetracker.utils.toCleanMoneyString

@Composable
fun EditCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit
) {
    var name by remember { mutableStateOf(category.name) }

    var icon by remember { mutableStateOf(category.icon) }

    var isIncome by remember { mutableStateOf(category.isIncome) }

    var monthlyBudget by remember(category.id) {
        mutableStateOf(
            category.monthlyBudget?.toCleanMoneyString() ?: ""
        )
    }

    val emojis = listOf(
        "🍔", "🍕", "☕", "🛒", "🚕", "⛽",
        "🏠", "💡", "📱", "💻", "🎮", "🎬",
        "⚽", "🏋️", "✈️", "🎁", "💰", "💳",
        "🏥", "📚", "👕", "🐶", "🚗", "🚌"
    )


    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Редактировать категорию")
        },

        text = {

            Column {

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("Название")
                    }
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Иконка",
                    style = MaterialTheme.typography.titleSmall
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    emojis.forEach { emoji ->

                        FilterChip(
                            selected = icon == emoji,
                            onClick = {
                                icon = emoji
                            },
                            label = {
                                Text(emoji)
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text("Тип")

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    FilterChip(
                        selected = !isIncome,
                        onClick = {
                            isIncome = false
                        },
                        label = {
                            Text("Расход")
                        }
                    )

                    FilterChip(
                        selected = isIncome,
                        onClick = {
                            isIncome = true
                        },
                        label = {
                            Text("Доход")
                        }
                    )
                }

                if (!isIncome) {
                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedTextField(
                        value = monthlyBudget,
                        onValueChange = { input ->
                            monthlyBudget = cleanMoneyInput(input)
                        },
                        label = {
                            Text("Бюджет на месяц")
                        }
                    )
                }
            }
        },

        confirmButton = {

            TextButton(
                enabled = name.isNotBlank(),
                onClick = {

                    onSave(
                        Category(
                            name = name.trim(),
                            icon = icon.trim(),
                            isIncome = isIncome,
                            monthlyBudget = if (isIncome) {
                                null
                            } else {
                                monthlyBudget.toDoubleOrNull()?.takeIf { it > 0.0 }
                            }
                        )
                    )
                }
            ) {
                Text("Сохранить")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Отмена")
            }
        }
    )
}