package com.example.myaiproject.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.example.myaiproject.presentation.screens.ai.AiScreen
import com.example.myaiproject.presentation.screens.analytics.AnalyticsScreen
import com.example.myaiproject.presentation.screens.auth.AuthScreen
import com.example.myaiproject.presentation.screens.goals.GoalsScreen
import com.example.myaiproject.presentation.screens.home.HomeScreen
import com.example.myaiproject.presentation.screens.profile.ProfileScreen
import com.example.myaiproject.presentation.screens.transactions.TransactionsScreen
import androidx.compose.foundation.layout.padding
import com.example.myaiproject.presentation.screens.home.HomeViewModel
import com.example.myaiproject.presentation.screens.transactions.TransactionsViewModel
import com.example.myaiproject.presentation.screens.analytics.AnalyticsViewModel
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Главная", Icons.Default.Home)
    object Transactions : Screen("transactions", "Операции", Icons.Default.List)
    object Analytics : Screen("analytics", "Аналитика", Icons.Default.BarChart)
    object Goals : Screen("goals", "Цели", Icons.Default.Flag)
    object Ai : Screen("ai", "AI", Icons.Default.Psychology)
    object Profile : Screen("profile", "Профиль", Icons.Default.Person)
}

@Composable
fun AppNavigation(viewModel: NavigationViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Пока проверяем токен — показываем экран загрузки
    if (isLoading) {
        androidx.compose.material3.CircularProgressIndicator()
        return
    }

    val bottomScreens = listOf(
        Screen.Home,
        Screen.Transactions,
        Screen.Analytics,
        Screen.Goals,
        Screen.Ai,
        Screen.Profile
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
            startDestination = if (isLoggedIn) "home" else "auth",
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable("auth") {
                AuthScreen(onAuthSuccess = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                LaunchedEffect(Unit) { viewModel.refresh() }
                HomeScreen(viewModel = viewModel)
            }
            composable(Screen.Transactions.route) {
                val viewModel: TransactionsViewModel = hiltViewModel()
                LaunchedEffect(Unit) { viewModel.refresh() }
                TransactionsScreen(viewModel = viewModel)
            }
            composable(Screen.Analytics.route) {
                val viewModel: AnalyticsViewModel = hiltViewModel()
                LaunchedEffect(Unit) { viewModel.refresh() }
                AnalyticsScreen(viewModel = viewModel)
            }
            composable(Screen.Goals.route) { GoalsScreen() }
            composable(Screen.Ai.route) { AiScreen() }
            composable(Screen.Profile.route) {
                ProfileScreen(onLogout = {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                })
            }
        }}
    }
