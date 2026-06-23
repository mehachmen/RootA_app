package com.roota.app.domain.usecase.task

import com.roota.app.domain.model.Dependency
import com.roota.app.domain.model.Task
import com.roota.app.domain.model.TaskStatus
import com.roota.app.domain.repository.DependencyRepository
import com.roota.app.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ChangeTaskStatusUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var dependencyRepository: DependencyRepository
    private lateinit var useCase: ChangeTaskStatusUseCase

    @Before
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        dependencyRepository = mockk()
        useCase = ChangeTaskStatusUseCase(taskRepository, dependencyRepository)
    }

    @Test
    fun completedWithIncompleteParent_throwsIllegalStateException() = runTest {
        val child = task(2, TaskStatus.IN_PROGRESS)
        val parent = task(1, TaskStatus.IN_PROGRESS)

        coEvery { taskRepository.getTaskById(2) } returns child
        coEvery { dependencyRepository.getDependenciesForTaskOnce(2) } returns listOf(
            Dependency(id = 1, sourceTaskId = 1, targetTaskId = 2)
        )
        coEvery { taskRepository.getTaskById(1) } returns parent

        val error = runCatching {
            useCase(taskId = 2, newStatus = TaskStatus.COMPLETED)
        }.exceptionOrNull()

        assertEquals("Сначала выполните родительские задачи!", error?.message)
    }

    @Test
    fun completedWithAllParentsDone_updatesTask() = runTest {
        val child = task(2, TaskStatus.IN_PROGRESS)
        val parent = task(1, TaskStatus.COMPLETED)
        val updated = slot<Task>()

        coEvery { taskRepository.getTaskById(2) } returns child
        coEvery { dependencyRepository.getDependenciesForTaskOnce(2) } returns listOf(
            Dependency(id = 1, sourceTaskId = 1, targetTaskId = 2)
        )
        coEvery { taskRepository.getTaskById(1) } returns parent
        coEvery { taskRepository.updateTask(capture(updated)) } returns Unit

        useCase(taskId = 2, newStatus = TaskStatus.COMPLETED)

        assertEquals(TaskStatus.COMPLETED, updated.captured.status)
    }

    @Test
    fun taskNotFound_throwsNoSuchElementException() = runTest {
        coEvery { taskRepository.getTaskById(99) } returns null

        val error = runCatching {
            useCase(taskId = 99, newStatus = TaskStatus.COMPLETED)
        }.exceptionOrNull()

        assertEquals("Задача с ID 99 не найдена", error?.message)
    }

    private fun task(id: Long, status: TaskStatus) = Task(
        id = id,
        projectId = 1,
        title = "Task $id",
        status = status
    )
}
