package com.roota.app.presentation.graph

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roota.app.domain.model.TaskStatus
import com.roota.app.domain.usecase.graph.AddDependencyUseCase
import com.roota.app.domain.usecase.graph.GetGraphUseCase
import com.roota.app.domain.usecase.graph.GraphLayoutUseCase
import com.roota.app.domain.usecase.project.GetProjectByIdUseCase
import com.roota.app.domain.usecase.project.UpdateProjectUseCase
import com.roota.app.domain.usecase.task.GetTaskUseCase
import com.roota.app.domain.usecase.task.UpdateTaskUseCase
import com.roota.app.presentation.graph.model.DependencyUi
import com.roota.app.presentation.graph.model.GraphState
import com.roota.app.presentation.graph.model.TaskUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GraphViewModel @Inject constructor(
    private val getGraphUseCase: GetGraphUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val addDependencyUseCase: AddDependencyUseCase,
    private val graphLayoutUseCase: GraphLayoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GraphState())
    val state = _state.asStateFlow()

    private var currentProjectId: Long = 0L
    private var saveViewportJob: Job? = null

    fun loadProject(projectId: Long) {
        currentProjectId = projectId
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val project = getProjectByIdUseCase(projectId)
            _state.value = _state.value.copy(
                projectTitle = project?.title ?: "",
                linkColorArgb = project?.linkColorArgb ?: 0xFF448AFFFFL,
                scale = project?.graphScale ?: 1f,
                offset = Offset(
                    project?.graphOffsetX ?: 0f,
                    project?.graphOffsetY ?: 0f
                )
            )

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

                val taskMap = taskUis.associateBy { it.id }
                val dependencyUis = graphData.dependencies.mapNotNull { dep ->
                    val source = taskMap[dep.sourceTaskId]
                    val target = taskMap[dep.targetTaskId]
                    if (source != null && target != null) {
                        DependencyUi(
                            sourceTaskId = dep.sourceTaskId,
                            targetTaskId = dep.targetTaskId,
                            sourceCompleted = source.status == TaskStatus.COMPLETED
                        )
                    } else null
                }

                _state.value = _state.value.copy(
                    tasks = taskUis,
                    dependencies = dependencyUis,
                    isLoading = false
                )
            }
        }
    }

    fun enterPickMode(pickForTaskId: Long, initialParents: List<Long> = emptyList()) {
        _state.value = _state.value.copy(
            isPickMode = true,
            pickForTaskId = pickForTaskId,
            pickedParentIds = initialParents.toSet()
        )
    }

    fun togglePickParent(taskId: Long) {
        if (!_state.value.isPickMode) return
        if (taskId == _state.value.pickForTaskId && _state.value.pickForTaskId != 0L) return

        val current = _state.value.pickedParentIds.toMutableSet()
        if (current.contains(taskId)) current.remove(taskId) else current.add(taskId)
        _state.value = _state.value.copy(pickedParentIds = current)
    }

    fun getPickedParentIds(): List<Long> = _state.value.pickedParentIds.toList()

    fun onCanvasDrag(dragAmount: Offset) {
        _state.value = _state.value.copy(offset = _state.value.offset + dragAmount)
        scheduleSaveViewport()
    }

    fun onScale(zoom: Float) {
        val newScale = (_state.value.scale * zoom).coerceIn(0.3f, 5f)
        _state.value = _state.value.copy(scale = newScale)
        scheduleSaveViewport()
    }

    fun zoomIn() = onScale(1.25f)

    fun zoomOut() = onScale(0.8f)

    fun onTaskPositionCommitted(taskId: Long, posX: Float, posY: Float) {
        _state.value = _state.value.copy(
            tasks = _state.value.tasks.map { task ->
                if (task.id == taskId) task.copy(posX = posX, posY = posY) else task
            }
        )
        viewModelScope.launch {
            try {
                val domainTask = getTaskUseCase(taskId) ?: return@launch
                updateTaskUseCase(domainTask.copy(posX = posX, posY = posY))
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun autoLayout() {
        viewModelScope.launch {
            try {
                val graphData = getGraphUseCase(currentProjectId).first()
                val laidOut = graphLayoutUseCase(graphData.tasks, graphData.dependencies)
                laidOut.forEach { updateTaskUseCase(it) }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun addDependency(sourceTaskId: Long, targetTaskId: Long) {
        viewModelScope.launch {
            try {
                addDependencyUseCase(sourceTaskId, targetTaskId)
            } catch (e: IllegalArgumentException) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun saveViewport() {
        if (currentProjectId == 0L) return
        viewModelScope.launch {
            val project = getProjectByIdUseCase(currentProjectId) ?: return@launch
            val s = _state.value
            updateProjectUseCase(
                project.copy(
                    graphScale = s.scale,
                    graphOffsetX = s.offset.x,
                    graphOffsetY = s.offset.y
                )
            )
        }
    }

    private fun scheduleSaveViewport() {
        saveViewportJob?.cancel()
        saveViewportJob = viewModelScope.launch {
            delay(400)
            saveViewport()
        }
    }

    override fun onCleared() {
        saveViewport()
        super.onCleared()
    }
}
