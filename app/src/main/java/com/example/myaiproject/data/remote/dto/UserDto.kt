package com.example.myaiproject.data.remote.dto

data class RegisterRequest(
    val email: String,
    val name: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class TokenResponse(
    val access_token: String,
    val token_type: String
)