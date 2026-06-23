package com.roota.app.data.local.entity

data class ProjectTaskStats(
    val projectId: Long,
    val taskCount: Int,
    val completedCount: Int
)
