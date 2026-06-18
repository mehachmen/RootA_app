package com.roota.app.presentation.graph

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roota.app.domain.usecase.graph.AddDependencyUseCase
import com.roota.app.domain.usecase.task.CreateTaskUseCase
import com.roota.app.domain.usecase.graph.GetGraphUseCase
import com.roota.app.presentation.graph.model.GraphState
import com.roota.app.presentation.graph.model.TaskUi
import com.roota.app.presentation.graph.model.DependencyUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GraphViewModel @Inject constructor(
    private val getGraphUseCase: GetGraphUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val addDependencyUseCase: AddDependencyUseCase
    // private val changeTaskStatusUseCase: ChangeTaskStatusUseCase,  // можно добавить позже
) : ViewModel() {

    private val _state = MutableStateFlow(GraphState())
    val state = _state.asStateFlow()

    // Загрузка графа проекта
    fun loadProject(projectId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            getGraphUseCase(projectId).collectLatest { graphData ->
                val taskUis = graphData.tasks.map { task ->
                    TaskUi(
                        id = task.id,
                        title = task.title,
                        posX = task.posX,
                        posY = task.posY,
                        status = task.status
                    )
                }

                val dependencyUis = graphData.dependencies.map { dep ->
                    // TODO: В будущем сопоставлять координаты узлов для отрисовки стрелок
                    DependencyUi(
                        start = Offset.Zero,
                        end = Offset.Zero
                    )
                }

                _state.value = _state.value.copy(
                    tasks = taskUis,
                    dependencies = dependencyUis,
                    isLoading = false
                )
            }
        }
    }

    // Добавление новой задачи
    fun addNewTask(projectId: Long) {
        viewModelScope.launch {
            try {
                val newTaskId = createTaskUseCase(
                    projectId = projectId,
                    title = "Новая задача ${state.value.tasks.size + 1}",
                    posX = 200f + (state.value.tasks.size * 60f) % 500f,
                    posY = 200f + (state.value.tasks.size * 50f) % 400f
                )

                // Перезагружаем граф после создания
                loadProject(projectId)
            } catch (e: Exception) {
                // TODO: Показать ошибку пользователю
                e.printStackTrace()
            }
        }
    }

    // Drag холста
    fun onDrag(dragAmount: Offset) {
        _state.value = _state.value.copy(
            offset = _state.value.offset + dragAmount
        )
    }

    // Zoom холста
    fun onScale(zoom: Float) {
        val newScale = (_state.value.scale * zoom).coerceIn(0.3f, 5f)
        _state.value = _state.value.copy(scale = newScale)
    }

    // Клик по задаче (открытие деталей)
    fun onTaskClick(taskId: Long) {
        // TODO: Передать taskId в TaskDetailSheet через навигацию или SharedViewModel
        println("Открыть детали задачи ID: $taskId")
        // Пример: navController.navigate("task_detail/$taskId")
    }

    // TODO: Добавить позже
    fun updateTaskPosition(taskId: Long, newPosX: Float, newPosY: Float) {
        // updateTaskUseCase + перезагрузка
    }

    fun addDependency(sourceTaskId: Long, targetTaskId: Long, projectId: Long) {
        viewModelScope.launch {
            try {
                addDependencyUseCase(sourceTaskId, targetTaskId)
                loadProject(projectId) // обновить граф
            } catch (e: IllegalArgumentException) {
                // Показать ошибку (циклическая зависимость и т.д.)
                e.printStackTrace()
            }
        }
    }
}