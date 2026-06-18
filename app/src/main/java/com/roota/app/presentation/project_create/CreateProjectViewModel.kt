package com.roota.app.presentation.project_create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roota.app.domain.usecase.project.CreateProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun onColorTagChange(argb: Long) {
        _state.value = _state.value.copy(colorTagArgb = argb)
    }

    fun onLinkColorChange(argb: Long) {
        _state.value = _state.value.copy(linkColorArgb = argb)
    }

    fun createProject(onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            if (state.value.title.isBlank()) return@launch
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val projectId = createProjectUseCase(
                    title = state.value.title,
                    description = state.value.description.ifBlank { null },
                    colorTagArgb = state.value.colorTagArgb,
                    linkColorArgb = state.value.linkColorArgb
                )
                _state.value = _state.value.copy(isLoading = false)
                onCreated(projectId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}

data class CreateProjectState(
    val title: String = "",
    val description: String = "",
    val colorTagArgb: Long = 0xFF39FF14L,
    val linkColorArgb: Long = 0xFF448AFFFFL,
    val isLoading: Boolean = false,
    val error: String? = null
)
