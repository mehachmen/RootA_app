package com.roota.app.presentation.project_create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.roota.app.domain.usecase.project.CreateProjectUseCase

@HiltViewModel
class CreateProjectViewModel @Inject constructor(
    private val createProjectUseCase: CreateProjectUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CreateProjectState())
    val state = _state.asStateFlow()

    fun onTitleChange(title: String) {
        _state.value = _state.value.copy(title = title)
    }

    fun onDescriptionChange(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    fun createProject() {
        viewModelScope.launch {
            if (state.value.title.isBlank()) return@launch
            createProjectUseCase(state.value.title, state.value.description)
        }
    }
}

data class CreateProjectState(
    val title: String = "",
    val description: String = ""
)