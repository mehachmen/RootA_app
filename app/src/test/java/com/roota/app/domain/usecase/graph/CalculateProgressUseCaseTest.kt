package com.roota.app.domain.usecase.graph

import com.roota.app.domain.model.Task
import com.roota.app.domain.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateProgressUseCaseTest {

    private val useCase = CalculateProgressUseCase()

    @Test
    fun `returns zero for empty task list`() {
        assertEquals(0f, useCase(emptyList()))
    }

    @Test
    fun `calculates progress percent correctly`() {
        val tasks = listOf(
            task(1, TaskStatus.COMPLETED),
            task(2, TaskStatus.COMPLETED),
            task(3, TaskStatus.NOT_STARTED),
            task(4, TaskStatus.IN_PROGRESS)
        )

        assertEquals(50f, useCase(tasks))
    }

    @Test
    fun `getStats returns correct counts`() {
        val tasks = listOf(
            task(1, TaskStatus.COMPLETED),
            task(2, TaskStatus.IN_PROGRESS),
            task(3, TaskStatus.NOT_STARTED)
        )

        val stats = useCase.getStats(tasks)

        assertEquals(3, stats.total)
        assertEquals(1, stats.completed)
        assertEquals(1, stats.inProgress)
        assertEquals(1, stats.notStarted)
    }

    private fun task(id: Long, status: TaskStatus) = Task(
        id = id,
        projectId = 1,
        title = "Task $id",
        status = status
    )
}
