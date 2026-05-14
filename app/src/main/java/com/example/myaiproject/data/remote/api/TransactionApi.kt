package com.example.myaiproject.data.remote.api

import com.example.myaiproject.data.remote.dto.TransactionDto
import retrofit2.http.*

interface TransactionApi {
    @GET("transactions/")
    suspend fun getTransactions(
        @Header("Authorization") token: String
    ): List<TransactionDto>

    @POST("transactions/")
    suspend fun createTransaction(
        @Header("Authorization") token: String,
        @Body transaction: TransactionDto
    ): TransactionDto

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )
}