package com.roota.app.domain.usecase.project

import com.roota.app.domain.model.Project
import com.roota.app.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProjectsUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    operator fun invoke(): Flow<List<Project>> {
        return projectRepository.getAllProjects()
    }
}