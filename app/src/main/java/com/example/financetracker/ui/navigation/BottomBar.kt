package com.example.financetracker.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Category
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
    val bottomBarItems = listOf(

        BottomItem(Routes.ADD, Icons.Default.Add, "Добавить"),
        BottomItem(Routes.DASHBOARD, Icons.Default.Home, "Главная"),
        BottomItem(Routes.ANALYTICS, Icons.Default.Analytics, "Аналитика")
    )

    NavigationBar {

        bottomBarItems.forEach { item ->

            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) }
            )
        }
    }
}