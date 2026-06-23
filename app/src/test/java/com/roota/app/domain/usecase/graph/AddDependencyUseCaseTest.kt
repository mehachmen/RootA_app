package com.roota.app.domain.usecase.graph

import com.roota.app.domain.repository.DependencyRepository
import com.roota.app.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AddDependencyUseCaseTest {

    private lateinit var dependencyRepository: DependencyRepository
    private lateinit var taskRepository: TaskRepository
    private lateinit var detectCycleUseCase: DetectCycleUseCase
    private lateinit var useCase: AddDependencyUseCase

    @Before
    fun setUp() {
        dependencyRepository = mockk(relaxed = true)
        taskRepository = mockk(relaxed = true)
        detectCycleUseCase = mockk()
        useCase = AddDependencyUseCase(dependencyRepository, taskRepository, detectCycleUseCase)
    }

    @Test
    fun selfDependency_throwsIllegalArgumentException() = runTest {
        val error = runCatching { useCase(sourceTaskId = 1, targetTaskId = 1) }.exceptionOrNull()

        assertEquals("Задача не может зависеть сама от себя", error?.message)
    }

    @Test
    fun cycleDetected_throwsIllegalArgumentException() = runTest {
        coEvery { detectCycleUseCase.invoke(1, 2) } returns true

        val error = runCatching { useCase(sourceTaskId = 1, targetTaskId = 2) }.exceptionOrNull()

        assertEquals("Циклическая зависимость невозможна!", error?.message)
    }

    @Test
    fun validDependency_savesToRepository() = runTest {
        coEvery { detectCycleUseCase.invoke(1, 2) } returns false

        useCase(sourceTaskId = 1, targetTaskId = 2)

        coVerify(exactly = 1) { dependencyRepository.addDependency(any()) }
    }
}
