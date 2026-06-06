package com.example.financetracker

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.financetracker.ui.analytics.AnalyticsScreen
import com.example.financetracker.ui.categories.CategoryScreen
import com.example.financetracker.ui.dashboard.DashboardScreen
import com.example.financetracker.ui.goals.GoalsScreen
import com.example.financetracker.ui.navigation.FinanceBottomBar
import com.example.financetracker.ui.navigation.Routes
import com.example.financetracker.ui.navigation.pages
import com.example.financetracker.ui.theme.FinanceTrackerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceTrackerApp(
    viewModel: FinanceViewModel,
    categoryViewModel: CategoryViewModel
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()
    var drawerRoute by remember { mutableStateOf<String?>(null) }

    fun navigateFromDrawer(route: String) {
        scope.launch {
            drawerState.close()
            drawerRoute = route
        }
    }

    FinanceTrackerTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen || (pagerState.currentPage == 0 && drawerRoute == null),
            drawerContent = {
                ModalDrawerSheet {
                    Text(
                        "Finance Tracker",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )

                    NavigationDrawerItem(
                        label = { Text("Категории") },
                        selected = drawerRoute == Routes.CATEGORIES,
                        onClick = { navigateFromDrawer(Routes.CATEGORIES) }
                    )

                    NavigationDrawerItem(
                        label = { Text("Настройки") },
                        selected = drawerRoute == "settings",
                        onClick = { navigateFromDrawer("settings") }
                    )
                }
            }
        ) {
            val currentTitle = when (drawerRoute) {
                Routes.CATEGORIES -> "Категории"
                else -> when (pages[pagerState.currentPage]) {
                    Routes.DASHBOARD -> "Главная"
                    Routes.ANALYTICS -> "Аналитика"
                    Routes.GOALS -> "Цели"
                    else -> "Finance Tracker"
                }
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(currentTitle)
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
                    val currentRoute = if (drawerRoute == null) {
                        pages[pagerState.currentPage]
                    } else {
                        null
                    }

                    FinanceBottomBar(
                        currentRoute = currentRoute,
                        onItemClick = { route ->
                            val pageIndex = pages.indexOf(route)

                            if (pageIndex != -1) {
                                drawerRoute = null
                                scope.launch {
                                    pagerState.animateScrollToPage(pageIndex)
                                }
                            }
                        }
                    )
                }
            ) { padding ->
                when (drawerRoute) {
                    Routes.CATEGORIES -> {
                        CategoryScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(padding)
                        )
                    }

                    else -> {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.padding(padding)
                        ) { page ->
                            when (pages[page]) {
                                Routes.DASHBOARD -> {
                                    DashboardScreen(
                                        viewModel = viewModel,
                                        categoryViewModel = categoryViewModel,
                                        navController = navController
                                    )
                                }

                                Routes.ANALYTICS -> {
                                    AnalyticsScreen(viewModel)
                                }

                                Routes.GOALS -> {
                                    GoalsScreen(viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
