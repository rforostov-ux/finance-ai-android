package com.example.myaiproject.presentation.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myaiproject.data.local.entity.GoalEntity
import com.example.myaiproject.data.local.entity.toDomain
import com.example.myaiproject.data.repository.GoalRepository
import com.example.myaiproject.domain.model.Goal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GoalsUiState(
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val repository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            repository.getGoals().collect { list ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        goals = list.map { entity -> entity.toDomain() }
                    )
                }
            }
        }
    }

    fun addGoal(title: String, targetAmount: Double) {
        viewModelScope.launch {
            repository.addGoal(
                GoalEntity(
                    id = System.currentTimeMillis().toInt(),
                    title = title,
                    targetAmount = targetAmount,
                    currentAmount = 0.0,
                    isCompleted = false,
                    deadline = null,
                    createdAt = System.currentTimeMillis().toString()
                )
            )
            _uiState.update { it.copy(showAddDialog = false) }
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }

    fun showDialog() = _uiState.update { it.copy(showAddDialog = true) }
    fun hideDialog() = _uiState.update { it.copy(showAddDialog = false) }
}