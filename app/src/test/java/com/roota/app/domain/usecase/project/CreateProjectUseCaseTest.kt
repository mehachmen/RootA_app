package com.roota.app.domain.usecase.project

import com.roota.app.domain.repository.ProjectRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CreateProjectUseCaseTest {

    private lateinit var projectRepository: ProjectRepository
    private lateinit var useCase: CreateProjectUseCase

    @Before
    fun setUp() {
        projectRepository = mockk()
        useCase = CreateProjectUseCase(projectRepository)
    }

    @Test
    fun blankTitle_throwsIllegalArgumentException() = runTest {
        val error = runCatching { useCase(title = "   ") }.exceptionOrNull()

        assertEquals("Название проекта не может быть пустым", error?.message)
    }

    @Test
    fun validTitle_trimsAndCreatesProject() = runTest {
        coEvery {
            projectRepository.createProject(
                title = "Диплом",
                description = null,
                colorTagArgb = any(),
                linkColorArgb = any()
            )
        } returns 42L

        val id = useCase(title = "  Диплом  ")

        assertEquals(42L, id)
        coVerify {
            projectRepository.createProject(
                title = "Диплом",
                description = null,
                colorTagArgb = any(),
                linkColorArgb = any()
            )
        }
    }
}
