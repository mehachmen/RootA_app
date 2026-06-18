package com.roota.app.data.mapper

import com.roota.app.data.local.entity.ProjectEntity
import com.roota.app.domain.model.Project

object ProjectMapper {

    fun toDomain(entity: ProjectEntity): Project {
        return Project(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: Project): ProjectEntity {
        return ProjectEntity(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            createdAt = domain.createdAt,
            updatedAt = System.currentTimeMillis()
        )
    }

    fun toDomainList(entities: List<ProjectEntity>): List<Project> {
        return entities.map { toDomain(it) }
    }
}