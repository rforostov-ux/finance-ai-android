package com.example.myaiproject.presentation.screens.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myaiproject.presentation.screens.home.BalanceCard

@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(
                text = "Аналитика",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Баланс
        item {
            BalanceCard(
                balance = uiState.balance,
                income = uiState.totalIncome,
                expense = uiState.totalExpense
            )
        }

        // Расходы по категориям
        item {
            Text(
                text = "Расходы по категориям",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (uiState.categoryStats.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет данных для анализа",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(uiState.categoryStats) { stat ->
                CategoryStatItem(stat)
            }
        }
    }
}

@Composable
fun CategoryStatItem(stat: CategoryStat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stat.category, fontWeight = FontWeight.Medium)
                Text(
                    text = "${String.format("%.0f", stat.amount)} ₽",
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (stat.percentage / 100.0).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${stat.percentage}%",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}