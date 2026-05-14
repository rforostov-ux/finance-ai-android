package com.example.myaiproject.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myaiproject.data.local.entity.toDomain
import com.example.myaiproject.data.remote.dto.TransactionDto
import com.example.myaiproject.data.repository.TransactionRepository
import com.example.myaiproject.domain.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            repository.syncWithServer()
            repository.getTransactions().collect { list ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        transactions = list.map { entity -> entity.toDomain() }
                    )
                }
            }
        }
    }

    fun addTransaction(amount: Double, type: String, category: String, description: String) {
        viewModelScope.launch {
            repository.createTransaction(
                TransactionDto(
                    amount = amount,
                    type = type,
                    category = category,
                    description = description.ifEmpty { null }
                )
            )
            _uiState.update { it.copy(showAddDialog = false) }
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun showDialog() = _uiState.update { it.copy(showAddDialog = true) }
    fun hideDialog() = _uiState.update { it.copy(showAddDialog = false) }
}