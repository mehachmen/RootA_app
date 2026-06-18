package com.roota.app.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roota.app.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    // private val calculateProgressUseCase: CalculateProgressUseCase,
    // private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressState())
    val state = _state.asStateFlow()

    fun loadProjectProgress(projectId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // TODO: Подключить UseCase
            // val tasks = getTasksUseCase.execute(projectId)

            // Временные мок-данные
            val mockTasks = listOf(
                TaskStatus.NOT_STARTED, TaskStatus.IN_PROGRESS, TaskStatus.COMPLETED,
                TaskStatus.COMPLETED, TaskStatus.NOT_STARTED, TaskStatus.IN_PROGRESS
            )

            val total = mockTasks.size
            val completed = mockTasks.count { it == TaskStatus.COMPLETED }
            val inProgress = mockTasks.count { it == TaskStatus.IN_PROGRESS }

            val progressPercent = if (total > 0) (completed.toFloat() / total) * 100 else 0f

            val notStarted = total - completed - inProgress

            _state.value = ProgressState(
                progressPercent = progressPercent,
                totalTasks = total,
                completedTasks = completed,
                inProgressTasks = inProgress,
                notStartedTasks = notStarted,
                isLoading = false
            )
        }
    }
}

data class ProgressState(
    val progressPercent: Float = 0f,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val notStartedTasks: Int = 0,   // ← Добавлено
    val isLoading: Boolean = false
)