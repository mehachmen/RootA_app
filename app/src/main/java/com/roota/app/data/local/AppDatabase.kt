package com.roota.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.roota.app.data.local.dao.DependencyDao
import com.roota.app.data.local.dao.ProjectDao
import com.roota.app.data.local.dao.TaskDao
import com.roota.app.data.local.dao.UserSettingsDao
import com.roota.app.data.local.entity.DependencyEntity
import com.roota.app.data.local.entity.ProjectEntity
import com.roota.app.data.local.entity.TaskEntity
import com.roota.app.data.local.entity.UserSettingsEntity

@Database(
    entities = [
        ProjectEntity::class,
        TaskEntity::class,
        DependencyEntity::class,
        UserSettingsEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun taskDao(): TaskDao
    abstract fun dependencyDao(): DependencyDao

    abstract fun userSettingsDao(): UserSettingsDao
}