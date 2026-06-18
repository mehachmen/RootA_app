package com.roota.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dependencies",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceTaskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["targetTaskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sourceTaskId", "targetTaskId"], unique = true)]
)
data class DependencyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceTaskId: Long,
    val targetTaskId: Long
)