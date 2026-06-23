package com.roota.app.domain.usecase.task

import com.roota.app.domain.model.Task
import com.roota.app.domain.model.TaskPriority
import com.roota.app.domain.model.TaskStatus
import com.roota.app.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CreateTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var useCase: CreateTaskUseCase

    @Before
    fun setUp() {
        taskRepository = mockk()
        useCase = CreateTaskUseCase(taskRepository)
    }

    @Test
    fun blankTitle_throwsIllegalArgumentException() = runTest {
        val error = runCatching {
            useCase(projectId = 1, title = "  ")
        }.exceptionOrNull()

        assertEquals("Название задачи не может быть пустым", error?.message)
    }

    @Test
    fun validTitle_createsTaskWithDefaults() = runTest {
        val captured = slot<Task>()
        coEvery { taskRepository.createTask(capture(captured)) } returns 7L

        val id = useCase(
            projectId = 1,
            title = "  Задача  ",
            priority = TaskPriority.HIGH
        )

        assertEquals(7L, id)
        assertEquals("Задача", captured.captured.title)
        assertEquals(TaskStatus.NOT_STARTED, captured.captured.status)
        assertEquals(TaskPriority.HIGH, captured.captured.priority)
        coVerify { taskRepository.createTask(any()) }
    }
}
