package com.roota.app.presentation.task_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roota.app.domain.model.Task
import com.roota.app.domain.model.TaskStatus
import com.roota.app.domain.usecase.task.CreateTaskUseCase
import com.roota.app.domain.usecase.task.UpdateTaskUseCase
import com.roota.app.domain.usecase.task.GetTasksByProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase   // ← Добавлено
) : ViewModel() {

    private val _state = MutableStateFlow(TaskEditState())
    val state = _state.asStateFlow()

    fun loadTaskForEdit(taskId: Long?, projectId: Long) {
        if (taskId == null) {
            _state.value = TaskEditState(isNewTask = true)
            loadAvailableTasks(projectId)
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // TODO: Добавить GetTaskUseCase
            _state.value = TaskEditState(
                task = Task(
                    id = taskId,
                    projectId = projectId,
                    title = "Редактируемая задача",
                    description = "Описание задачи...",
                    status = TaskStatus.NOT_STARTED,
                    posX = 300f,
                    posY = 250f
                ),
                isNewTask = false,
                isLoading = false
            )
            loadAvailableTasks(projectId)
        }
    }

    private fun loadAvailableTasks(projectId: Long) {
        viewModelScope.launch {
            getTasksByProjectUseCase(projectId).collect { tasks ->
                _state.value = _state.value.copy(
                    availableTasks = tasks.filter { it.id != _state.value.task?.id } // исключаем саму себя
                )
            }
        }
    }

    fun updateTitle(title: String) {
        _state.value = _state.value.copy(
            task = _state.value.task?.copy(title = title) ?: Task(
                id = 0,
                projectId = 1,
                title = title,
                status = TaskStatus.NOT_STARTED
            )
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

    fun addParentDependency(parentId: Long) {
        val current = _state.value.selectedParentIds.toMutableList()
        if (!current.contains(parentId)) {
            current.add(parentId)
            _state.value = _state.value.copy(selectedParentIds = current)
        }
    }

    fun removeParentDependency(parentId: Long) {
        val current = _state.value.selectedParentIds.toMutableList()
        current.remove(parentId)
        _state.value = _state.value.copy(selectedParentIds = current)
    }

    fun saveTask(projectId: Long) {
        viewModelScope.launch {
            _state.value.task?.let { task ->
                val taskToSave = task.copy(projectId = projectId)

                try {
                    if (_state.value.isNewTask) {
                        createTaskUseCase(
                            projectId = projectId,
                            title = taskToSave.title,
                            description = taskToSave.description,
                            posX = taskToSave.posX,
                            posY = taskToSave.posY
                        )
                    } else {
                        updateTaskUseCase(taskToSave)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

data class TaskEditState(
    val task: Task? = null,
    val isNewTask: Boolean = true,
    val isLoading: Boolean = false,
    val availableTasks: List<Task> = emptyList(),           // ← Добавлено
    val selectedParentIds: List<Long> = emptyList()         // ← Добавлено
)