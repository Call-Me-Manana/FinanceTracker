package com.example.financetracker.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.data.Category

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit
) {

    var name by remember { mutableStateOf("") }

    var icon by remember { mutableStateOf("📁") }

    var isIncome by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Новая категория")
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

                OutlinedTextField(
                    value = icon,
                    onValueChange = {
                        icon = it
                    },
                    label = {
                        Text("Иконка")
                    }
                )

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
                            isIncome = isIncome
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