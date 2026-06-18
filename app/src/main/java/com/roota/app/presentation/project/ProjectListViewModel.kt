package com.roota.app.presentation.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roota.app.domain.model.Project
import com.roota.app.domain.usecase.project.CreateProjectUseCase
import com.roota.app.domain.usecase.project.DeleteProjectUseCase
import com.roota.app.domain.usecase.project.GetProjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectListViewModel @Inject constructor(
    private val getProjectsUseCase: GetProjectsUseCase,
    private val createProjectUseCase: CreateProjectUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getProjectsUseCase().collect { projects ->
                _uiState.update {
                    it.copy(projects = projects, isLoading = false)
                }
            }
        }
    }

    fun createProject(title: String, description: String?) {
        viewModelScope.launch {
            try {
                createProjectUseCase(title, description)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            try {
                deleteProjectUseCase(project)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
data class ProjectListUiState(
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)