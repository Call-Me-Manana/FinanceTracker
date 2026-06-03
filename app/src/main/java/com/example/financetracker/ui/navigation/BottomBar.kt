package com.example.financetracker.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.navigation.NavController

data class BottomItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun FinanceBottomBar(
    navController: NavController,
    currentRoute: String?
) {
    val items = listOf(
        BottomItem(Routes.DASHBOARD, Icons.Default.Home, "Главная"),
        BottomItem(Routes.ADD, Icons.Default.Add, "Добавить")
    )

    NavigationBar {

        items.forEach { item ->

            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Routes.DASHBOARD)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) }
            )
        }
    }
}