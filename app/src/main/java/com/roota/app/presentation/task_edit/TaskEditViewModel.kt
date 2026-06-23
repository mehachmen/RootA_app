package com.roota.app.presentation.task_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roota.app.domain.model.Task
import com.roota.app.domain.model.TaskPriority
import com.roota.app.domain.model.TaskStatus
import com.roota.app.domain.repository.DependencyRepository
import com.roota.app.domain.usecase.graph.AddDependencyUseCase
import com.roota.app.domain.usecase.task.ChangeTaskStatusUseCase
import com.roota.app.domain.usecase.task.CreateTaskUseCase
import com.roota.app.domain.usecase.task.DeleteTaskUseCase
import com.roota.app.domain.usecase.task.GetTaskUseCase
import com.roota.app.domain.usecase.task.GetTasksByProjectUseCase
import com.roota.app.domain.usecase.task.UpdateTaskUseCase
import com.roota.app.presentation.ui.util.DateFormatters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val addDependencyUseCase: AddDependencyUseCase,
    private val changeTaskStatusUseCase: ChangeTaskStatusUseCase,
    private val dependencyRepository: DependencyRepository,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TaskEditState())
    val state = _state.asStateFlow()

    fun loadTaskForEdit(taskId: Long?, projectId: Long) {
        if (taskId == null) {
            _state.value = TaskEditState(
                isNewTask = true,
                task = Task(
                    id = 0,
                    projectId = projectId,
                    title = "",
                    status = TaskStatus.NOT_STARTED
                )
            )
            loadAvailableTasks(projectId)
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val task = getTaskUseCase(taskId)
                if (task == null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Задача не найдена"
                    )
                    return@launch
                }

                val parentIds = dependencyRepository.getDependenciesForTaskOnce(taskId)
                    .filter { it.targetTaskId == taskId }
                    .map { it.sourceTaskId }

                _state.value = TaskEditState(
                    task = task,
                    isNewTask = false,
                    isLoading = false,
                    selectedParentIds = parentIds,
                    deadlineInput = DateFormatters.formatDeadlineInput(task.deadline)
                )
                loadAvailableTasks(projectId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun loadAvailableTasks(projectId: Long) {
        viewModelScope.launch {
            getTasksByProjectUseCase(projectId).collect { tasks ->
                _state.value = _state.value.copy(
                    availableTasks = tasks.filter { it.id != _state.value.task?.id }
                )
            }
        }
    }

    fun updateTitle(title: String) {
        _state.value = _state.value.copy(
            task = _state.value.task?.copy(title = title),
            titleError = null
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

    fun updatePriority(priority: TaskPriority) {
        _state.value = _state.value.copy(
            task = _state.value.task?.copy(priority = priority)
        )
    }

    fun updateDeadlineInput(input: String) {
        _state.value = _state.value.copy(
            deadlineInput = DateFormatters.applyDeadlineMask(input.filter { it.isDigit() })
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

    fun saveTask(projectId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val task = _state.value.task ?: return@launch
            if (task.title.isBlank()) {
                val message = "Название задачи не может быть пустым"
                _state.value = _state.value.copy(titleError = message, error = message)
                return@launch
            }

            val deadline = DateFormatters.parseDeadlineInput(_state.value.deadlineInput)
            val validationError = if (_state.value.deadlineInput.isNotBlank()) {
                DateFormatters.validateDeadlineInput(_state.value.deadlineInput)
            } else null

            if (validationError != null) {
                _state.value = _state.value.copy(error = validationError)
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val taskId = if (_state.value.isNewTask) {
                    val id = createTaskUseCase(
                        projectId = projectId,
                        title = task.title,
                        description = task.description,
                        posX = task.posX,
                        posY = task.posY,
                        priority = task.priority,
                        deadline = deadline
                    )
                    if (task.status == TaskStatus.COMPLETED) {
                        changeTaskStatusUseCase(id, TaskStatus.COMPLETED)
                    } else if (task.status != TaskStatus.NOT_STARTED) {
                        getTaskUseCase(id)?.let { created ->
                            updateTaskUseCase(created.copy(status = task.status))
                        }
                    }
                    id
                } else {
                    val existing = getTaskUseCase(task.id)
                    if (task.status == TaskStatus.COMPLETED && existing?.status != TaskStatus.COMPLETED) {
                        changeTaskStatusUseCase(task.id, TaskStatus.COMPLETED)
                    }
                    updateTaskUseCase(
                        task.copy(projectId = projectId, deadline = deadline)
                    )
                    task.id
                }

                syncDependencies(taskId)
                _state.value = _state.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private suspend fun syncDependencies(taskId: Long) {
        val desired = _state.value.selectedParentIds.toSet()
        val existing = dependencyRepository.getDependenciesForTaskOnce(taskId)
            .filter { it.targetTaskId == taskId }

        val currentParentIds = existing.map { it.sourceTaskId }.toSet()

        existing.filter { it.sourceTaskId !in desired }.forEach { dep ->
            dependencyRepository.deleteDependency(dep)
        }

        (desired - currentParentIds).forEach { parentId ->
            addDependencyUseCase(parentId, taskId)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun setSelectedParentIds(parentIds: List<Long>) {
        _state.value = _state.value.copy(selectedParentIds = parentIds)
    }

    fun deleteTask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val task = _state.value.task ?: return@launch
            if (_state.value.isNewTask) return@launch
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                deleteTaskUseCase(task)
                _state.value = _state.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}

data class TaskEditState(
    val task: Task? = null,
    val isNewTask: Boolean = true,
    val isLoading: Boolean = false,
    val availableTasks: List<Task> = emptyList(),
    val selectedParentIds: List<Long> = emptyList(),
    val deadlineInput: String = "",
    val titleError: String? = null,
    val error: String? = null
)
