package com.example.myaiproject.domain.model

data class Transaction(
    val id: Int,
    val amount: Double,
    val type: String,
    val category: String,
    val description: String?,
    val date: String
)