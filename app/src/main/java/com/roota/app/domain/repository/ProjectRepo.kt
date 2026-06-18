package com.roota.app.domain.repository

import com.roota.app.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProjects(): Flow<List<Project>>
    suspend fun getProjectById(id: Long): Project?
    suspend fun createProject(title: String, description: String?): Long
    suspend fun updateProject(project: Project)
    suspend fun deleteProject(project: Project)
}