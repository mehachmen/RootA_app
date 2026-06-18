package com.roota.app.domain.usecase.task

import com.roota.app.domain.model.Task
import com.roota.app.domain.model.TaskStatus
import com.roota.app.domain.repository.TaskRepository
import javax.inject.Inject

class ChangeTaskStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long, newStatus: TaskStatus) {
        val task = taskRepository.getTaskById(taskId)
            ?: throw NoSuchElementException("Задача с ID $taskId не найдена")

        val updatedTask = task.copy(status = newStatus)
        taskRepository.updateTask(updatedTask)
    }
}