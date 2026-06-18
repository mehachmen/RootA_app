package com.roota.app.presentation.graph.model

import androidx.compose.ui.geometry.Offset
import com.roota.app.domain.model.TaskStatus

data class GraphState(
    val tasks: List<TaskUi> = emptyList(),
    val dependencies: List<DependencyUi> = emptyList(),
    val scale: Float = 1f,
    val offset: Offset = Offset.Zero,
    val isLoading: Boolean = false
)

data class TaskUi(
    val id: Long,
    val title: String,
    val posX: Float,
    val posY: Float,
    val status: TaskStatus
)

data class DependencyUi(
    val start: Offset,
    val end: Offset
)