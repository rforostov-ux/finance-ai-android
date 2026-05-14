package com.example.myaiproject.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myaiproject.domain.model.Transaction

@Composable
fun TransactionItem(transaction: Transaction) {
    val isIncome = transaction.type == "income"
    val amountColor = if (isIncome) Color(0xFF4CAF50) else Color(0xFFF44336)
    val amountPrefix = if (isIncome) "+" else "-"

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.category,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                if (!transaction.description.isNullOrEmpty()) {
                    Text(
                        text = transaction.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = transaction.date.take(10),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$amountPrefix${transaction.amount} ₽",
                color = amountColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}