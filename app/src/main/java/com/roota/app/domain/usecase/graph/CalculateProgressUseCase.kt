package com.roota.app.domain.usecase.graph

import com.roota.app.domain.model.Task
import com.roota.app.domain.model.TaskStatus
import javax.inject.Inject

class CalculateProgressUseCase @Inject constructor() {

    operator fun invoke(tasks: List<Task>): Float {
        if (tasks.isEmpty()) return 0f

        val completed = tasks.count { it.status == TaskStatus.COMPLETED }
        return (completed.toFloat() / tasks.size) * 100
    }

    fun getStats(tasks: List<Task>): TaskStats {
        val total = tasks.size
        val completed = tasks.count { it.status == TaskStatus.COMPLETED }
        val inProgress = tasks.count { it.status == TaskStatus.IN_PROGRESS }

        return TaskStats(
            total = total,
            completed = completed,
            inProgress = inProgress,
            notStarted = total - completed - inProgress
        )
    }
}

data class TaskStats(
    val total: Int,
    val completed: Int,
    val inProgress: Int,
    val notStarted: Int
)