package com.roota.app.domain.usecase.project

import com.roota.app.domain.model.Project
import com.roota.app.domain.repository.ProjectRepository
import javax.inject.Inject

class GetProjectByIdUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(id: Long): Project? = projectRepository.getProjectById(id)
}
