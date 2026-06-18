package com.roota.app.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roota.app.domain.usecase.graph.CalculateProgressUseCase
import com.roota.app.domain.usecase.task.GetTasksByProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val calculateProgressUseCase: CalculateProgressUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressState())
    val state = _state.asStateFlow()

    fun loadProjectProgress(projectId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            getTasksByProjectUseCase(projectId).collect { tasks ->
                val stats = calculateProgressUseCase.getStats(tasks)
                val progressPercent = calculateProgressUseCase(tasks)

                _state.value = ProgressState(
                    progressPercent = progressPercent,
                    totalTasks = stats.total,
                    completedTasks = stats.completed,
                    inProgressTasks = stats.inProgress,
                    notStartedTasks = stats.notStarted,
                    isLoading = false
                )
            }
        }
    }
}

data class ProgressState(
    val progressPercent: Float = 0f,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val notStartedTasks: Int = 0,
    val isLoading: Boolean = false
)
