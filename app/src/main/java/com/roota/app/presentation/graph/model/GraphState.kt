package com.roota.app.presentation.graph.model

import androidx.compose.ui.geometry.Offset
import com.roota.app.domain.model.TaskStatus

data class GraphState(
    val tasks: List<TaskUi> = emptyList(),
    val dependencies: List<DependencyUi> = emptyList(),
    val scale: Float = 1f,
    val offset: Offset = Offset.Zero,
    val isLoading: Boolean = false,
    val error: String? = null,
    val draggingTaskId: Long? = null,
    val isPickMode: Boolean = false,
    val pickForTaskId: Long = 0L,
    val pickedParentIds: Set<Long> = emptySet(),
    val projectTitle: String = "",
    val linkColorArgb: Long = 0xFF448AFFFFL
)

data class TaskUi(
    val id: Long,
    val title: String,
    val posX: Float,
    val posY: Float,
    val status: TaskStatus
)

data class DependencyUi(
    val sourceTaskId: Long,
    val targetTaskId: Long,
    val sourceCompleted: Boolean = false
)
