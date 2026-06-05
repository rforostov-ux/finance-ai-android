package com.example.myaiproject.presentation.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myaiproject.data.local.entity.toDomain
import com.example.myaiproject.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryStat(
    val category: String,
    val amount: Double,
    val percentage: Double
)

data class AnalyticsUiState(
    val balance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.syncWithServer()
        }
    }
    private fun loadAnalytics() {
        viewModelScope.launch {
            repository.getTransactions().collect { list ->
                val domain = list.map { it.toDomain() }
                val income = domain.filter { it.type == "income" }.sumOf { it.amount }
                val expense = domain.filter { it.type == "expense" }.sumOf { it.amount }

                val categoryTotals = domain
                    .filter { it.type == "expense" }
                    .groupBy { it.category }
                    .mapValues { (_, v) -> v.sumOf { it.amount } }

                val total = categoryTotals.values.sum().takeIf { it > 0 } ?: 1.0

                val stats = categoryTotals.entries
                    .sortedByDescending { it.value }
                    .map { (cat, amt) ->
                        CategoryStat(
                            category = cat,
                            amount = amt,
                            percentage = (amt / total * 100).let {
                                String.format("%.1f", it).toDouble()
                            }
                        )
                    }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalIncome = income,
                        totalExpense = expense,
                        balance = income - expense,
                        categoryStats = stats
                    )
                }
            }
        }
    }
}