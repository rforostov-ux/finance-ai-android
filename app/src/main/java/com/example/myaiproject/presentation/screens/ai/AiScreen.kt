package com.example.myaiproject.presentation.screens.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AiScreen(viewModel: AiViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Заголовок
        Text(
            text = "AI Ассистент",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )

        // Кнопка анализа
        Button(
            onClick = { viewModel.analyzeFinances() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            enabled = !uiState.isLoading
        ) {
            Text("Анализировать расходы")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Список сообщений
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (uiState.messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Задайте вопрос или нажмите\n\"Анализировать расходы\"",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            items(uiState.messages) { message ->
                ChatBubble(message = message)
            }
            if (uiState.isLoading) {
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
        }

        // Поле ввода
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.inputText,
                onValueChange = { viewModel.updateInput(it) },
                label = { Text("Ваш вопрос") },
                modifier = Modifier.weight(1f),
                maxLines = 3
            )
            IconButton(
                onClick = { viewModel.sendMessage() },
                enabled = uiState.inputText.isNotEmpty() && !uiState.isLoading
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Отправить",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser)
            Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = if (message.isUser)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}