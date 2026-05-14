package com.example.myaiproject.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myaiproject.data.remote.api.AiApi
import com.example.myaiproject.data.remote.api.ChatRequest
import com.example.myaiproject.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(val text: String, val isUser: Boolean)

data class AiUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class AiViewModel @Inject constructor(
    private val aiApi: AiApi,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiUiState())
    val uiState = _uiState.asStateFlow()

    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val message = _uiState.value.inputText.trim()
        if (message.isEmpty()) return

        _uiState.update {
            it.copy(
                messages = it.messages + ChatMessage(message, isUser = true),
                inputText = "",
                isLoading = true
            )
        }

        viewModelScope.launch {
            try {
                val token = authRepository.getToken() ?: return@launch
                val response = aiApi.chat(
                    "Bearer $token",
                    ChatRequest(message = message)
                )
                _uiState.update {
                    it.copy(
                        messages = it.messages + ChatMessage(response.response, isUser = false),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        messages = it.messages + ChatMessage(
                            "Ошибка: проверьте подключение к серверу",
                            isUser = false
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun analyzeFinances() {
        _uiState.update {
            it.copy(
                messages = it.messages + ChatMessage(
                    "Проанализируй мои расходы за последний месяц",
                    isUser = true
                ),
                isLoading = true
            )
        }

        viewModelScope.launch {
            try {
                val token = authRepository.getToken() ?: return@launch
                val response = aiApi.analyze("Bearer $token")
                _uiState.update {
                    it.copy(
                        messages = it.messages + ChatMessage(response.analysis, isUser = false),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        messages = it.messages + ChatMessage(
                            "Ошибка: проверьте подключение к серверу",
                            isUser = false
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }
}