package com.roota.app.domain.repository

import com.roota.app.domain.model.Dependency
import kotlinx.coroutines.flow.Flow

interface DependencyRepository {
    fun getDependenciesForTask(taskId: Long): Flow<List<Dependency>>

    suspend fun getDependenciesForTaskOnce(taskId: Long): List<Dependency>

    fun getAllDependenciesForProject(projectId: Long): Flow<List<Dependency>>

    suspend fun getAllDependencies(): List<Dependency>

    suspend fun addDependency(dependency: Dependency)

    suspend fun deleteDependency(dependency: Dependency)
}
