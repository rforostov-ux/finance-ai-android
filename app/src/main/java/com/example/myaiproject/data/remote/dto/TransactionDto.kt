package com.example.myaiproject.data.remote.dto

import com.example.myaiproject.data.local.entity.TransactionEntity
import com.google.gson.annotations.SerializedName

data class TransactionDto(
    val id: Int = 0,
    val amount: Double,
    val type: String,
    val category: String,
    val description: String? = null,
    @SerializedName("user_id")
    val userId: Int = 0
)

fun TransactionDto.toEntity() = TransactionEntity(
    id = id,
    amount = amount,
    type = type,
    category = category,
    description = description,
    date = "",
    userId = userId
)