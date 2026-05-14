package com.example.myaiproject.data.repository

import com.example.myaiproject.data.local.dao.TransactionDao
import com.example.myaiproject.data.local.entity.TransactionEntity
import com.example.myaiproject.data.remote.api.TransactionApi
import com.example.myaiproject.data.remote.dto.TransactionDto
import com.example.myaiproject.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val api: TransactionApi,
    private val dao: TransactionDao,
    private val authRepository: AuthRepository
) {
    fun getTransactions(): Flow<List<TransactionEntity>> = dao.getAll()

    suspend fun syncWithServer() {
        try {
            val token = authRepository.getToken() ?: return
            val remote = api.getTransactions("Bearer $token")
            dao.upsertAll(remote.map { it.toEntity() })
        } catch (e: Exception) {
            // Нет интернета — показываем кэш
        }
    }

    suspend fun createTransaction(dto: TransactionDto): Result<Unit> {
        return try {
            val token = authRepository.getToken()
                ?: return Result.failure(Exception("Не авторизован"))
            val created = api.createTransaction("Bearer $token", dto)
            dao.upsert(created.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(id: Int): Result<Unit> {
        return try {
            val token = authRepository.getToken()
                ?: return Result.failure(Exception("Не авторизован"))
            api.deleteTransaction("Bearer $token", id)
            dao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}