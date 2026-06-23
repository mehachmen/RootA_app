package com.roota.app.domain.usecase.graph

import com.roota.app.domain.model.Dependency
import com.roota.app.domain.model.Task
import javax.inject.Inject

class GraphLayoutUseCase @Inject constructor() {

    operator fun invoke(
        tasks: List<Task>,
        dependencies: List<Dependency>
    ): List<Task> {
        if (tasks.isEmpty()) return emptyList()

        val taskIds = tasks.map { it.id }.toSet()
        val inDegree = taskIds.associateWith { 0 }.toMutableMap()
        val graph = mutableMapOf<Long, MutableList<Long>>()

        taskIds.forEach { graph[it] = mutableListOf() }

        dependencies.forEach { dep ->
            if (dep.sourceTaskId in taskIds && dep.targetTaskId in taskIds) {
                graph.getOrPut(dep.sourceTaskId) { mutableListOf() }.add(dep.targetTaskId)
                inDegree[dep.targetTaskId] = (inDegree[dep.targetTaskId] ?: 0) + 1
            }
        }

        val layers = mutableListOf<MutableList<Long>>()
        var currentLayer = inDegree.filter { it.value == 0 }.keys.toMutableList()

        if (currentLayer.isEmpty()) {
            currentLayer = tasks.map { it.id }.toMutableList()
            layers.add(currentLayer)
        } else {
            while (currentLayer.isNotEmpty()) {
                layers.add(currentLayer)
                val nextLayer = mutableListOf<Long>()
                currentLayer.forEach { nodeId ->
                    graph[nodeId]?.forEach { childId ->
                        inDegree[childId] = (inDegree[childId] ?: 0) - 1
                        if (inDegree[childId] == 0) {
                            nextLayer.add(childId)
                        }
                    }
                }
                currentLayer = nextLayer
            }
        }

        val placed = layers.flatten().toSet()
        val remaining = tasks.filter { it.id !in placed }
        if (remaining.isNotEmpty()) {
            layers.add(remaining.map { it.id }.toMutableList())
        }

        val taskMap = tasks.associateBy { it.id }
        val horizontalSpacing = 180f
        val verticalSpacing = 130f
        val startX = 160f
        val startY = 160f

        return layers.flatMapIndexed { layerIndex, layer ->
            layer.mapIndexedNotNull { indexInLayer, taskId ->
                taskMap[taskId]?.copy(
                    posX = startX + indexInLayer * horizontalSpacing,
                    posY = startY + layerIndex * verticalSpacing
                )
            }
        }
    }
}
