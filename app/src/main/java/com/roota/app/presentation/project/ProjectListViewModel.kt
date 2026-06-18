package com.roota.app.presentation.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roota.app.data.local.dao.TaskDao
import com.roota.app.domain.usecase.project.DeleteProjectUseCase
import com.roota.app.domain.usecase.project.GetProjectByIdUseCase
import com.roota.app.domain.usecase.project.GetProjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectListViewModel @Inject constructor(
    private val getProjectsUseCase: GetProjectsUseCase,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val taskDao: TaskDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            combine(
                getProjectsUseCase(),
                taskDao.getTaskStatsByProject()
            ) { projects, stats ->
                val statsMap = stats.associateBy { it.projectId }
                val cards = projects.map { project ->
                    val stat = statsMap[project.id]
                    val taskCount = stat?.taskCount ?: 0
                    val completedCount = stat?.completedCount ?: 0
                    val progress = if (taskCount > 0) {
                        (completedCount.toFloat() / taskCount) * 100f
                    } else 0f
                    ProjectCardUi(
                        id = project.id,
                        title = project.title,
                        description = project.description,
                        colorTagArgb = project.colorTagArgb,
                        taskCount = taskCount,
                        progressPercent = progress
                    )
                }
                ProjectListUiState(
                    projects = cards,
                    projectCount = cards.size,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }

    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            try {
                val project = getProjectByIdUseCase(projectId) ?: return@launch
                deleteProjectUseCase(project)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class ProjectCardUi(
    val id: Long,
    val title: String,
    val description: String?,
    val colorTagArgb: Long,
    val taskCount: Int,
    val progressPercent: Float
)

data class ProjectListUiState(
    val projects: List<ProjectCardUi> = emptyList(),
    val projectCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)
