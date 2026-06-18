package com.roota.app.domain.usecase.task

import com.roota.app.domain.repository.TaskRepository
import com.roota.app.domain.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksByProjectUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(projectId: Long): Flow<List<Task>> {
        return taskRepository.getTasksByProject(projectId)
    }
}