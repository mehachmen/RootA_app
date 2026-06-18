package com.roota.app.data.repo

import com.roota.app.data.local.dao.DependencyDao
import com.roota.app.data.mapper.DependencyMapper
import com.roota.app.domain.model.Dependency
import com.roota.app.domain.repository.DependencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DependencyRepositoryImpl @Inject constructor(
    private val dependencyDao: DependencyDao,
    private val mapper: DependencyMapper
) : DependencyRepository {

    override fun getDependenciesForTask(taskId: Long): Flow<List<Dependency>> =
        dependencyDao.getDependenciesForTask(taskId).map { mapper.toDomainList(it) }

    override suspend fun getDependenciesForTaskOnce(taskId: Long): List<Dependency> =
        mapper.toDomainList(dependencyDao.getDependenciesForTaskOnce(taskId))

    override fun getAllDependenciesForProject(projectId: Long): Flow<List<Dependency>> =
        dependencyDao.getDependenciesByProjectId(projectId).map { mapper.toDomainList(it) }

    override suspend fun getAllDependencies(): List<Dependency> =
        mapper.toDomainList(dependencyDao.getAllDependencies())

    override suspend fun addDependency(dependency: Dependency) {
        dependencyDao.insertDependency(mapper.toEntity(dependency))
    }

    override suspend fun deleteDependency(dependency: Dependency) {
        dependencyDao.deleteDependency(mapper.toEntity(dependency))
    }
}
