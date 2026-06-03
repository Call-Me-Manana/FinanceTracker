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
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financetracker.ui.dashboard.DashboardScreen
import com.example.financetracker.ui.navigation.FinanceBottomBar
import com.example.financetracker.ui.navigation.FinanceNavGraph


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = DatabaseProvider.getDatabase(this)

        val viewModel = FinanceViewModel(
            database.transactionDao()
        )

        val categoryViewModel = CategoryViewModel(
            database.categoryDao()
        )

        setContent {
            val navController = rememberNavController()

            Scaffold(
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

