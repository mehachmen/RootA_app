package com.roota.app.presentation.task_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roota.app.domain.model.Task
import com.roota.app.domain.repository.DependencyRepository
import com.roota.app.domain.usecase.task.GetTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTaskUseCase: GetTaskUseCase,
    private val dependencyRepository: DependencyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state = _state.asStateFlow()

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val task = getTaskUseCase(taskId)
            val parentTasks = if (task != null) {
                val deps = dependencyRepository.getDependenciesForTaskOnce(taskId)
                    .filter { it.targetTaskId == taskId }
                deps.mapNotNull { dep -> getTaskUseCase(dep.sourceTaskId) }
            } else emptyList()

            _state.value = _state.value.copy(
                task = task,
                parentTasks = parentTasks,
                isLoading = false,
                error = if (task == null) "Задача не найдена" else null
            )
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

data class TaskDetailState(
    val task: Task? = null,
    val parentTasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
