package com.example.myaiproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myaiproject.domain.model.Goal

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val isCompleted: Boolean,
    val deadline: String?,
    val createdAt: String
)

fun GoalEntity.toDomain() = Goal(
    id = id,
    title = title,
    targetAmount = targetAmount,
    currentAmount = currentAmount,
    isCompleted = isCompleted,
    deadline = deadline
)