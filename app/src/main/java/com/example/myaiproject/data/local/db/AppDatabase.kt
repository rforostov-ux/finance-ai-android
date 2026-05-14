package com.example.myaiproject.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myaiproject.data.local.dao.GoalDao
import com.example.myaiproject.data.local.dao.TransactionDao
import com.example.myaiproject.data.local.entity.GoalEntity
import com.example.myaiproject.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, GoalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao
}