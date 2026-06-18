package com.roota.app.data.local.dao

import androidx.room.*
import com.roota.app.data.local.entity.DependencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DependencyDao {
    @Query("SELECT * FROM dependencies WHERE sourceTaskId = :taskId OR targetTaskId = :taskId")
    fun getDependenciesForTask(taskId: Long): Flow<List<DependencyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDependency(dependency: DependencyEntity)

    @Delete
    suspend fun deleteDependency(dependency: DependencyEntity)
}