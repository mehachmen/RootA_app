package com.roota.app.domain.repository

import com.roota.app.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasksByProject(projectId: Long): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun createTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
}