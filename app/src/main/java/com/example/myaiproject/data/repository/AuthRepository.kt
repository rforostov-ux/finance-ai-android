package com.example.myaiproject.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.myaiproject.data.remote.api.AuthApi
import com.example.myaiproject.data.remote.dto.LoginRequest
import com.example.myaiproject.data.remote.dto.RegisterRequest
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    suspend fun register(email: String, name: String, password: String): Result<Unit> {
        return try {
            val response = api.register(RegisterRequest(email, name, password))
            saveToken(response.access_token)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = api.login(LoginRequest(email, password))
            saveToken(response.access_token)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun getToken(): String? {
        return dataStore.data.first()[TOKEN_KEY]
    }

    suspend fun logout() {
        dataStore.edit { it.remove(TOKEN_KEY) }
    }

    suspend fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}