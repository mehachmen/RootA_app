package com.roota.app.domain.usecase.project

import com.roota.app.domain.model.Project
import com.roota.app.domain.repository.ProjectRepository
import javax.inject.Inject

class UpdateProjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(project: Project) {
        projectRepository.updateProject(project)
    }
}
