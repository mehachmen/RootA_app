package com.roota.app.data.repo

import com.roota.app.data.local.dao.ProjectDao
import com.roota.app.data.mapper.ProjectMapper
import com.roota.app.domain.repository.ProjectRepository
import com.roota.app.domain.model.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao,
    private val mapper: ProjectMapper
) : ProjectRepository {

    override fun getAllProjects(): Flow<List<Project>> =
        projectDao.getAllProjects().map { mapper.toDomainList(it) }

    override suspend fun getProjectById(id: Long): Project? =
        projectDao.getProjectById(id)?.let { mapper.toDomain(it) }

    override suspend fun createProject(
        title: String,
        description: String?,
        colorTagArgb: Long,
        linkColorArgb: Long
    ): Long {
        val project = Project(
            title = title,
            description = description,
            colorTagArgb = colorTagArgb,
            linkColorArgb = linkColorArgb
        )
        return projectDao.insertProject(mapper.toEntity(project))
    }

    override suspend fun updateProject(project: Project) {
        projectDao.updateProject(mapper.toEntity(project))
    }

    override suspend fun deleteProject(project: Project) {
        projectDao.deleteProject(mapper.toEntity(project))
    }
}