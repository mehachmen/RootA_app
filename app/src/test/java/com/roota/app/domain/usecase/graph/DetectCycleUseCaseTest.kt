package com.roota.app.domain.usecase.graph

import com.roota.app.domain.model.Dependency
import com.roota.app.domain.repository.DependencyRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DetectCycleUseCaseTest {

    private lateinit var dependencyRepository: DependencyRepository
    private lateinit var useCase: DetectCycleUseCase

    @Before
    fun setUp() {
        dependencyRepository = mockk()
        useCase = DetectCycleUseCase(dependencyRepository)
    }

    @Test
    fun `returns true when new edge creates cycle`() = runTest {
        coEvery { dependencyRepository.getAllDependencies() } returns listOf(
            Dependency(id = 1, sourceTaskId = 1, targetTaskId = 2),
            Dependency(id = 2, sourceTaskId = 2, targetTaskId = 3)
        )

        val hasCycle = useCase(sourceTaskId = 3, targetTaskId = 1)

        assertTrue(hasCycle)
    }

    @Test
    fun `returns false when new edge does not create cycle`() = runTest {
        coEvery { dependencyRepository.getAllDependencies() } returns listOf(
            Dependency(id = 1, sourceTaskId = 1, targetTaskId = 2)
        )

        val hasCycle = useCase(sourceTaskId = 1, targetTaskId = 3)

        assertFalse(hasCycle)
    }

    @Test
    fun `returns false when no existing dependencies`() = runTest {
        coEvery { dependencyRepository.getAllDependencies() } returns emptyList()

        val hasCycle = useCase(sourceTaskId = 1, targetTaskId = 2)

        assertFalse(hasCycle)
    }
}
