package com.roota.app.domain.usecase.task

import com.roota.app.domain.model.Task
import com.roota.app.domain.repository.TaskRepository
import javax.inject.Inject

class GetTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long): Task? {
        return taskRepository.getTaskById(taskId)
    }
}
