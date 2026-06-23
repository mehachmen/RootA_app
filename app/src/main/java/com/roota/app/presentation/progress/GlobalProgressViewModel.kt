package com.roota.app.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roota.app.data.local.dao.TaskDao
import com.roota.app.domain.usecase.graph.CalculateProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalProgressViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val calculateProgressUseCase: CalculateProgressUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            taskDao.getAllTasks().collect { entities ->
                val tasks = entities.map { entity ->
                    com.roota.app.data.mapper.TaskMapper.toDomain(entity)
                }
                val stats = calculateProgressUseCase.getStats(tasks)
                _state.value = ProgressState(
                    progressPercent = calculateProgressUseCase(tasks),
                    totalTasks = stats.total,
                    completedTasks = stats.completed,
                    inProgressTasks = stats.inProgress,
                    notStartedTasks = stats.notStarted,
                    isLoading = false
                )
            }
        }
    }
}
