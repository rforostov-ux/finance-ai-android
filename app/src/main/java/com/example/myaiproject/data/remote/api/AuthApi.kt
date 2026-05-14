package com.example.myaiproject.data.remote.api

import com.example.myaiproject.data.remote.dto.LoginRequest
import com.example.myaiproject.data.remote.dto.RegisterRequest
import com.example.myaiproject.data.remote.dto.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): TokenResponse

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse
}