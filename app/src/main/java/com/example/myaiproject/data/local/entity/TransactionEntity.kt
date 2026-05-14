package com.example.myaiproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myaiproject.domain.model.Transaction

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: Int,
    val amount: Double,
    val type: String,
    val category: String,
    val description: String?,
    val date: String,
    val userId: Int
)

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    amount = amount,
    type = type,
    category = category,
    description = description,
    date = date
)