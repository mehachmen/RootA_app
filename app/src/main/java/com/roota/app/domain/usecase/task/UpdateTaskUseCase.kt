package com.roota.app.domain.usecase.task

import com.roota.app.domain.model.Task
import com.roota.app.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        if (task.title.isBlank()) {
            throw IllegalArgumentException("Название задачи не может быть пустым")
        }
        taskRepository.updateTask(task)
    }
}