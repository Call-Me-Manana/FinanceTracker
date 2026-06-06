package com.example.financetracker.ui.dashboard.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = query,
        onValueChange = onQueryChange,
        label = {
            Text("Поиск")
        },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge
    )
}
