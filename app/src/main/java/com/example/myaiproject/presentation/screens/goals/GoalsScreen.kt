package com.example.myaiproject.presentation.screens.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myaiproject.domain.model.Goal

@Composable
fun GoalsScreen(viewModel: GoalsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.goals.isEmpty() -> {
                Text(
                    text = "Нет целей.\nНажмите + чтобы добавить",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        Text(
                            text = "Мои цели",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(uiState.goals, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            onDelete = { viewModel.deleteGoal(goal.id) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.showDialog() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 100.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Добавить цель")
        }
    }

    if (uiState.showAddDialog) {
        AddGoalDialog(
            onDismiss = { viewModel.hideDialog() },
            onConfirm = { title, amount -> viewModel.addGoal(title, amount) }
        )
    }
}

@Composable
fun GoalCard(goal: Goal, onDelete: () -> Unit) {
    val progress = if (goal.targetAmount > 0)
        (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${String.format("%.0f", goal.currentAmount)} ₽",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${String.format("%.0f", goal.targetAmount)} ₽",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "${(progress * 100).toInt()}% выполнено",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая цель") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название цели") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Целевая сумма") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    suffix = { Text("₽") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull() ?: return@Button
                    onConfirm(title, amountDouble)
                },
                enabled = title.isNotEmpty() && amount.isNotEmpty()
            ) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}