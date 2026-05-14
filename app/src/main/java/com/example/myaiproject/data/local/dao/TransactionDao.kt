package com.example.myaiproject.data.local.dao

import androidx.room.*
import com.example.myaiproject.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Upsert
    suspend fun upsertAll(transactions: List<TransactionEntity>)

    @Upsert
    suspend fun upsert(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}