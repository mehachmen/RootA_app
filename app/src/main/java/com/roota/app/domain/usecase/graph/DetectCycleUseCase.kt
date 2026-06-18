package com.roota.app.domain.usecase.graph

import com.roota.app.domain.model.Dependency
import com.roota.app.domain.repository.DependencyRepository
import javax.inject.Inject

class DetectCycleUseCase @Inject constructor(
    private val dependencyRepository: DependencyRepository
) {
    suspend operator fun invoke(sourceTaskId: Long, targetTaskId: Long): Boolean {
        val dependencies = dependencyRepository.getAllDependencies() // Нужно добавить метод в репозиторий

        val graph = buildGraph(dependencies)
        return hasCycle(graph, targetTaskId, sourceTaskId, mutableSetOf())
    }

    private fun buildGraph(dependencies: List<Dependency>): Map<Long, MutableList<Long>> {
        val graph = mutableMapOf<Long, MutableList<Long>>()
        dependencies.forEach { dep ->
            graph.getOrPut(dep.sourceTaskId) { mutableListOf() }.add(dep.targetTaskId)
        }
        return graph
    }

    private fun hasCycle(
        graph: Map<Long, List<Long>>,
        current: Long,
        target: Long,
        visited: MutableSet<Long>
    ): Boolean {
        if (current == target) return true
        if (visited.contains(current)) return false

        visited.add(current)

        graph[current]?.forEach { neighbor ->
            if (hasCycle(graph, neighbor, target, visited)) {
                return true
            }
        }
        return false
    }
}