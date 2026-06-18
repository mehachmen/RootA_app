package com.roota.app.data.local.dao

import androidx.room.*
import com.roota.app.data.local.entity.ProjectTaskStats
import com.roota.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE projectId = :projectId")
    fun getTasksByProject(projectId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("""
        SELECT projectId, COUNT(*) as taskCount,
        SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completedCount
        FROM tasks GROUP BY projectId
    """)
    fun getTaskStatsByProject(): Flow<List<ProjectTaskStats>>
}