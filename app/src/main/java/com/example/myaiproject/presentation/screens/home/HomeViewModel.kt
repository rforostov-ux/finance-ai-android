package com.example.myaiproject.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myaiproject.data.local.entity.toDomain
import com.example.myaiproject.data.repository.TransactionRepository
import com.example.myaiproject.domain.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val balance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun refresh() {
        viewModelScope.launch {
            transactionRepository.syncWithServer()
        }
    }
    fun loadData() {
        viewModelScope.launch {
            transactionRepository.syncWithServer()
            transactionRepository.getTransactions().collect { list ->
                val domainList = list.map { it.toDomain() }
                val income = domainList.filter { it.type == "income" }.sumOf { it.amount }
                val expense = domainList.filter { it.type == "expense" }.sumOf { it.amount }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalIncome = income,
                        totalExpense = expense,
                        balance = income - expense,
                        recentTransactions = domainList.take(5)
                    )
                }
            }
        }
    }
}