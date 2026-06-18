package com.roota.app.domain.usecase.graph

import com.roota.app.domain.model.Dependency
import com.roota.app.domain.repository.TaskRepository
import com.roota.app.domain.repository.DependencyRepository
import javax.inject.Inject

class AddDependencyUseCase @Inject constructor(
    private val dependencyRepository: DependencyRepository,
    private val taskRepository: TaskRepository,
    private val detectCycleUseCase: DetectCycleUseCase
) {
    suspend operator fun invoke(sourceTaskId: Long, targetTaskId: Long) {
        if (sourceTaskId == targetTaskId) {
            throw IllegalArgumentException("Задача не может зависеть сама от себя")
        }

        // Проверка на циклическую зависимость
        if (detectCycleUseCase.invoke(sourceTaskId, targetTaskId)) {
            throw IllegalArgumentException("Создание этой связи приведёт к циклической зависимости!")
        }

        val dependency = Dependency(
            sourceTaskId = sourceTaskId,
            targetTaskId = targetTaskId
        )

        dependencyRepository.addDependency(dependency)
    }
}