package com.example.myaiproject.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.*
import com.example.myaiproject.presentation.screens.auth.AuthScreen
import com.example.myaiproject.presentation.screens.home.HomeScreen
import com.example.myaiproject.presentation.screens.transactions.TransactionsScreen
import com.example.myaiproject.presentation.screens.analytics.AnalyticsScreen
import com.example.myaiproject.presentation.screens.goals.GoalsScreen
import com.example.myaiproject.presentation.screens.ai.AiScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Главная", Icons.Default.Home)
    object Transactions : Screen("transactions", "Операции", Icons.Default.List)
    object Analytics : Screen("analytics", "Аналитика", Icons.Default.BarChart)
    object Goals : Screen("goals", "Цели", Icons.Default.Flag)
    object Ai : Screen("ai", "AI", Icons.Default.Psychology)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val bottomScreens = listOf(
        Screen.Home,
        Screen.Transactions,
        Screen.Analytics,
        Screen.Goals,
        Screen.Ai
    )
    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute != "auth"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomScreens.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "auth"
        ) {
            composable("auth") {
                AuthScreen(
                    onAuthSuccess = {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Transactions.route) { TransactionsScreen() }
            composable(Screen.Analytics.route) { AnalyticsScreen() }
            composable(Screen.Goals.route) { GoalsScreen() }
            composable(Screen.Ai.route) { AiScreen() }
        }
    }
}