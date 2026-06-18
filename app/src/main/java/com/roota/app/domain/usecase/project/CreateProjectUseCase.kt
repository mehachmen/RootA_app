package com.roota.app.domain.usecase.project

import com.roota.app.domain.model.Project
import com.roota.app.domain.repository.ProjectRepository
import javax.inject.Inject

class CreateProjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(title: String, description: String? = null): Long {
        if (title.isBlank()) {
            throw IllegalArgumentException("Название проекта не может быть пустым")
        }

        return projectRepository.createProject(
            title = title.trim(),
            description = description?.trim()
        )
    }
}