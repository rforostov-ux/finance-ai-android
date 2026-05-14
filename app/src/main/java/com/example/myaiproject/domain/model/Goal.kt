package com.example.myaiproject.domain.model

data class Goal(
    val id: Int,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val isCompleted: Boolean,
    val deadline: String?
)