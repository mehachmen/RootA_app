package com.roota.app.domain.model

data class Task(
    val id: Long = 0,
    val projectId: Long,
    val title: String,
    val description: String? = null,
    val status: TaskStatus = TaskStatus.NOT_STARTED,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val deadline: Long? = null,
    val posX: Float = 100f,
    val posY: Float = 100f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
