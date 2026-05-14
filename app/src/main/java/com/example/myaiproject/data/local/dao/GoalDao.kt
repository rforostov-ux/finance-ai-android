package com.example.myaiproject.data.local.dao

import androidx.room.*
import com.example.myaiproject.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAll(): Flow<List<GoalEntity>>

    @Upsert
    suspend fun upsertAll(goals: List<GoalEntity>)

    @Upsert
    suspend fun upsert(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteById(id: Int)
}