package com.roota.app.data.local.dao

import androidx.room.*
import com.roota.app.data.local.entity.DependencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DependencyDao {
    @Query("SELECT * FROM dependencies WHERE sourceTaskId = :taskId OR targetTaskId = :taskId")
    fun getDependenciesForTask(taskId: Long): Flow<List<DependencyEntity>>

    @Query("SELECT * FROM dependencies WHERE sourceTaskId = :taskId OR targetTaskId = :taskId")
    suspend fun getDependenciesForTaskOnce(taskId: Long): List<DependencyEntity>

    @Query("SELECT * FROM dependencies")
    suspend fun getAllDependencies(): List<DependencyEntity>

    @Query(
        """
        SELECT d.* FROM dependencies d
        INNER JOIN tasks src ON d.sourceTaskId = src.id
        INNER JOIN tasks tgt ON d.targetTaskId = tgt.id
        WHERE src.projectId = :projectId AND tgt.projectId = :projectId
        """
    )
    fun getDependenciesByProjectId(projectId: Long): Flow<List<DependencyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDependency(dependency: DependencyEntity)

    @Delete
    suspend fun deleteDependency(dependency: DependencyEntity)
}
