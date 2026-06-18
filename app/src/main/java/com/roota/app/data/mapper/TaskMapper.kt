package com.roota.app.data.mapper

import com.roota.app.data.local.entity.TaskEntity
import com.roota.app.domain.model.Task
import com.roota.app.domain.model.TaskStatus

object TaskMapper {

    fun toDomain(entity: TaskEntity): Task {
        return Task(
            id = entity.id,
            projectId = entity.projectId,
            title = entity.title,
            description = entity.description,
            status = TaskStatus.valueOf(entity.status),
            posX = entity.posX,
            posY = entity.posY,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Task): TaskEntity {
        return TaskEntity(
            id = domain.id,
            projectId = domain.projectId,
            title = domain.title,
            description = domain.description,
            status = domain.status.name,
            posX = domain.posX,
            posY = domain.posY,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun toDomainList(entities: List<TaskEntity>): List<Task> {
        return entities.map { toDomain(it) }
    }
}