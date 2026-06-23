package com.roota.app.domain.usecase.graph

import com.roota.app.domain.model.Dependency
import com.roota.app.domain.model.Task
import com.roota.app.domain.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GraphLayoutUseCaseTest {

    private val useCase = GraphLayoutUseCase()

    @Test
    fun `assigns layers topologically`() {
        val tasks = listOf(
            task(1), task(2), task(3)
        )
        val deps = listOf(
            Dependency(sourceTaskId = 1, targetTaskId = 2),
            Dependency(sourceTaskId = 2, targetTaskId = 3)
        )

        val result = useCase(tasks, deps)
        val task2 = result.first { it.id == 2L }
        val task3 = result.first { it.id == 3L }

        assertTrue(task2.posY < task3.posY)
    }

    @Test
    fun `returns empty for empty input`() {
        assertTrue(useCase(emptyList(), emptyList()).isEmpty())
    }

    private fun task(id: Long) = Task(
        id = id,
        projectId = 1,
        title = "Task $id",
        status = TaskStatus.NOT_STARTED
    )
}
