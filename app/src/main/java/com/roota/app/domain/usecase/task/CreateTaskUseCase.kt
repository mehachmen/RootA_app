package com.roota.app.domain.usecase.task

import com.roota.app.domain.model.Task
import com.roota.app.domain.model.TaskPriority
import com.roota.app.domain.model.TaskStatus
import com.roota.app.domain.repository.TaskRepository
import javax.inject.Inject

class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(
        projectId: Long,
        title: String,
        description: String? = null,
        posX: Float = 150f,
        posY: Float = 150f,
        priority: TaskPriority = TaskPriority.MEDIUM,
        deadline: Long? = null
    ): Long {
        if (title.isBlank()) {
            throw IllegalArgumentException("Название задачи не может быть пустым")
        }

        val task = Task(
            projectId = projectId,
            title = title.trim(),
            description = description?.trim(),
            status = TaskStatus.NOT_STARTED,
            priority = priority,
            deadline = deadline,
            posX = posX,
            posY = posY
        )

        return taskRepository.createTask(task)
    }
}
