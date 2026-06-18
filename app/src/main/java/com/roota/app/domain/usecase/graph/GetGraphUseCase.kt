package com.roota.app.domain.usecase.graph

import com.roota.app.domain.model.Task
import com.roota.app.domain.model.Dependency
import com.roota.app.domain.repository.DependencyRepository
import com.roota.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetGraphUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val dependencyRepository: DependencyRepository
) {
    operator fun invoke(projectId: Long): Flow<GraphData> {
        return combine(
            taskRepository.getTasksByProject(projectId),
            dependencyRepository.getAllDependenciesForProject(projectId)
        ) { tasks, dependencies ->
            GraphData(tasks, dependencies)
        }
    }
}

data class GraphData(
    val tasks: List<Task>,
    val dependencies: List<Dependency>
)