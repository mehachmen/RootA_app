package com.roota.app.presentation.task_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roota.app.domain.model.Task
import com.roota.app.domain.model.TaskStatus
import com.roota.app.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val updateTaskUseCase: UpdateTaskUseCase
    // private val getTaskUseCase: GetTaskUseCase  // добавим позже
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state = _state.asStateFlow()

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // TODO: Подключить GetTaskUseCase
            _state.value = _state.value.copy(
                task = Task(
                    id = taskId,
                    projectId = 1,
                    title = "Пример задачи",
                    description = "Это описание задачи...",
                    status = TaskStatus.IN_PROGRESS,
                    posX = 200f,
                    posY = 200f
                ),
                isLoading = false
            )
        }
    }

    fun updateTitle(title: String) {
        _state.value = _state.value.copy(
            task = _state.value.task?.copy(title = title)
        )
    }

    fun updateDescription(description: String) {
        _state.value = _state.value.copy(
            task = _state.value.task?.copy(description = description)
        )
    }

    fun updateStatus(status: TaskStatus) {
        _state.value = _state.value.copy(
            task = _state.value.task?.copy(status = status)
        )
    }

    fun saveChanges() {
        viewModelScope.launch {
            _state.value.task?.let { task ->
                try {
                    updateTaskUseCase(task)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

data class TaskDetailState(
    val task: Task? = null,
    val isLoading: Boolean = false
)