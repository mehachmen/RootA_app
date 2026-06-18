package com.roota.app.domain.usecase.task

import com.roota.app.domain.model.TaskStatus
import com.roota.app.domain.repository.DependencyRepository
import com.roota.app.domain.repository.TaskRepository
import javax.inject.Inject

class ChangeTaskStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val dependencyRepository: DependencyRepository
) {
    suspend operator fun invoke(taskId: Long, newStatus: TaskStatus) {
        val task = taskRepository.getTaskById(taskId)
            ?: throw NoSuchElementException("Задача с ID $taskId не найдена")

        if (newStatus == TaskStatus.COMPLETED) {
            val parentIds = dependencyRepository.getDependenciesForTaskOnce(taskId)
                .filter { it.targetTaskId == taskId }
                .map { it.sourceTaskId }

            for (parentId in parentIds) {
                val parent = taskRepository.getTaskById(parentId)
                if (parent?.status != TaskStatus.COMPLETED) {
                    throw IllegalStateException("Сначала выполните родительские задачи!")
                }
            }
        }

        taskRepository.updateTask(task.copy(status = newStatus, updatedAt = System.currentTimeMillis()))
    }
}
