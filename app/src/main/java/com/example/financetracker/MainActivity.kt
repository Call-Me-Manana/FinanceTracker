package com.example.financetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.collectAsState
import com.example.financetracker.data.Transaction
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financetracker.repository.CategoryRepository
import com.example.financetracker.ui.dashboard.DashboardScreen
import com.example.financetracker.ui.navigation.FinanceBottomBar
import com.example.financetracker.ui.navigation.FinanceNavGraph
import com.example.financetracker.ui.theme.FinanceTrackerTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = DatabaseProvider.getDatabase(this)
        val categoryRepository = CategoryRepository(
            database.categoryDao()
        )
        val viewModel = FinanceViewModel(
            database.transactionDao(),
            categoryRepository
        )

        val categoryViewModel = CategoryViewModel(
            database.categoryDao()
        )


        setContent {
            FinanceTrackerTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(
                    initialValue = DrawerValue.Closed
                )

                val scope = rememberCoroutineScope()
                fun navigateFromDrawer(route: String) {
                    scope.launch {
                        drawerState.close()
                        navController.navigate(route)
                    }
                }
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Text(
                                "Finance Tracker",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleLarge
                            )

                            NavigationDrawerItem(
                                label = { Text("Категории") },
                                selected = false,
                                onClick = { navigateFromDrawer("categories") }
                            )

                            NavigationDrawerItem(
                                label = { Text("Цели") },
                                selected = false,
                                onClick = { navigateFromDrawer("goals") }
                            )

                            NavigationDrawerItem(
                                label = { Text("Настройки") },
                                selected = false,
                                onClick = { navigateFromDrawer("settings") }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text("Finance Tracker")
                                },

                                navigationIcon = {

                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Menu,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            val backStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = backStackEntry?.destination?.route

                            FinanceBottomBar(navController, currentRoute)
                        }

                    ) { padding ->

                        FinanceNavGraph(
                            navController = navController,
                            viewModel = viewModel,
                            categoryViewModel = categoryViewModel,
                            modifier = Modifier.padding(padding)
                        )
                    }
                }
            }

        }
    }
}

