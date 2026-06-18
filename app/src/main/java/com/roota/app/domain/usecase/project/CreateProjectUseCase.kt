package com.roota.app.domain.usecase.project

import com.roota.app.domain.repository.ProjectRepository
import javax.inject.Inject

class CreateProjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String? = null,
        colorTagArgb: Long = 0xFF39FF14L,
        linkColorArgb: Long = 0xFF448AFFFFL
    ): Long {
        if (title.isBlank()) {
            throw IllegalArgumentException("Название проекта не может быть пустым")
        }

        return projectRepository.createProject(
            title = title.trim(),
            description = description?.trim(),
            colorTagArgb = colorTagArgb,
            linkColorArgb = linkColorArgb
        )
    }
}
