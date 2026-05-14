package com.example.myaiproject.data.repository

import com.example.myaiproject.data.local.dao.GoalDao
import com.example.myaiproject.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoalRepository @Inject constructor(
    private val dao: GoalDao,
    private val authRepository: AuthRepository
) {
    fun getGoals(): Flow<List<GoalEntity>> = dao.getAll()

    suspend fun addGoal(goal: GoalEntity): Result<Unit> {
        return try {
            dao.upsert(goal)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteGoal(id: Int): Result<Unit> {
        return try {
            dao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}