package com.example.myaiproject.presentation.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myaiproject.data.local.entity.GoalEntity
import com.example.myaiproject.data.local.entity.toDomain
import com.example.myaiproject.data.remote.dto.TransactionDto
import com.example.myaiproject.data.repository.GoalRepository
import com.example.myaiproject.data.repository.TransactionRepository
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
    val showAddDialog: Boolean = false,
    val showDepositDialog: Boolean = false,
    val selectedGoalId: Int? = null
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val repository: GoalRepository,
    private val transactionRepository: TransactionRepository  // добавили
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

    fun showDepositDialog(goalId: Int) {
        _uiState.update { it.copy(showDepositDialog = true, selectedGoalId = goalId) }
    }

    fun hideDepositDialog() {
        _uiState.update { it.copy(showDepositDialog = false, selectedGoalId = null) }
    }

    fun depositToGoal(amount: Double) {
        viewModelScope.launch {
            val goalId = _uiState.value.selectedGoalId ?: return@launch
            val goal = _uiState.value.goals.find { it.id == goalId } ?: return@launch
            val newAmount = (goal.currentAmount + amount).coerceAtMost(goal.targetAmount)

            // Обновляем прогресс цели
            repository.addGoal(
                GoalEntity(
                    id = goal.id,
                    title = goal.title,
                    targetAmount = goal.targetAmount,
                    currentAmount = newAmount,
                    isCompleted = newAmount >= goal.targetAmount,
                    deadline = goal.deadline,
                    createdAt = System.currentTimeMillis().toString()
                )
            )

            // Автоматически создаём расход чтобы баланс уменьшился
            transactionRepository.createTransaction(
                TransactionDto(
                    amount = amount,
                    type = "expense",
                    category = "Накопления",
                    description = "Пополнение цели: ${goal.title}"
                )
            )

            hideDepositDialog()
        }
    }

    fun showDialog() = _uiState.update { it.copy(showAddDialog = true) }
    fun hideDialog() = _uiState.update { it.copy(showAddDialog = false) }
}