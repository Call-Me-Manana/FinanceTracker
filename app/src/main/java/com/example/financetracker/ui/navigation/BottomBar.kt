package com.example.financetracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun FinanceBottomBar(
    onItemClick: (String) -> Unit,
    currentRoute: String?
) {
    val bottomBarItems = listOf(
        BottomItem(Routes.DASHBOARD, Icons.Default.Home, "Главная"),
        BottomItem(Routes.ANALYTICS, Icons.Default.Analytics, "Аналитика"),
        BottomItem(Routes.GOALS, Icons.Default.Flag, "Цели")
    )

    NavigationBar {

        bottomBarItems.forEach { item ->

            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    onItemClick(item.route)
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) }
            )
        }
    }
}
