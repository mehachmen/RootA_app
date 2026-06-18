package com.roota.app.data.repo

import com.roota.app.data.local.dao.TaskDao
import com.roota.app.data.mapper.TaskMapper
import com.roota.app.domain.repository.TaskRepository
import com.roota.app.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val mapper: TaskMapper
) : TaskRepository {

    override fun getTasksByProject(projectId: Long): Flow<List<Task>> =
        taskDao.getTasksByProject(projectId).map { mapper.toDomainList(it) }

    override suspend fun getTaskById(id: Long): Task? =
        taskDao.getTaskById(id)?.let { mapper.toDomain(it) }

    override suspend fun createTask(task: Task): Long {
        return taskDao.insertTask(mapper.toEntity(task))
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(mapper.toEntity(task))
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(mapper.toEntity(task))
    }
}