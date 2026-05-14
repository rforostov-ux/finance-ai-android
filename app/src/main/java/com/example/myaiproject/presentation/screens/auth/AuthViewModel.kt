package com.example.myaiproject.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myaiproject.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isLoginMode: Boolean = true
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.login(email, password)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false, error = "Неверный email или пароль") }
                }
            )
        }
    }

    fun register(email: String, name: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.register(email, name, password)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false, error = "Ошибка регистрации") }
                }
            )
        }
    }

    fun toggleMode() {
        _uiState.update { it.copy(isLoginMode = !it.isLoginMode, error = null) }
    }
}