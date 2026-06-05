package com.example.myaiproject.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myaiproject.data.repository.AuthRepository
import com.example.myaiproject.data.repository.TransactionRepository
import com.example.myaiproject.data.repository.GoalRepository
import com.example.myaiproject.data.local.entity.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val totalTransactions: Int = 0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val totalGoals: Int = 0,
    val completedGoals: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val transactions = transactionRepository.getTransactions().first()
            val goals = goalRepository.getGoals().first()

            val income = transactions
                .filter { it.type == "income" }
                .sumOf { it.amount }
            val expense = transactions
                .filter { it.type == "expense" }
                .sumOf { it.amount }

            _uiState.update {
                it.copy(
                    totalTransactions = transactions.size,
                    totalIncome = income,
                    totalExpense = expense,
                    totalGoals = goals.size,
                    completedGoals = goals.count { g -> g.isCompleted },
                    isLoading = false
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}