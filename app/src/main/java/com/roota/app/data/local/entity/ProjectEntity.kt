package com.roota.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val colorTagArgb: Long = ProjectEntity.DEFAULT_COLOR_TAG,
    val linkColorArgb: Long = 0xFF448AFFFFL,
    val graphScale: Float = 1f,
    val graphOffsetX: Float = 0f,
    val graphOffsetY: Float = 0f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val DEFAULT_COLOR_TAG = 0xFF39FF14L
    }
}
