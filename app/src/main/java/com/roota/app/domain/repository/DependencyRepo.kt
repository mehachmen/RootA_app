package com.roota.app.domain.repository

import com.roota.app.domain.model.Dependency
import kotlinx.coroutines.flow.Flow

interface DependencyRepository {
    fun getDependenciesForTask(taskId: Long): Flow<List<Dependency>>

    // Новый метод
    fun getAllDependenciesForProject(projectId: Long): Flow<List<Dependency>>

    // Для DetectCycle (можно сделать suspend)
    suspend fun getAllDependencies(): List<Dependency>

    suspend fun addDependency(dependency: Dependency)
    suspend fun deleteDependency(dependency: Dependency)
}