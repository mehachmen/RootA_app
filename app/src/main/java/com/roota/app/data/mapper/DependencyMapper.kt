package com.roota.app.data.mapper

import com.roota.app.data.local.entity.DependencyEntity
import com.roota.app.domain.model.Dependency

object DependencyMapper {
    fun toDomain(entity: DependencyEntity): Dependency = Dependency(
        id = entity.id,
        sourceTaskId = entity.sourceTaskId,
        targetTaskId = entity.targetTaskId
    )

    fun toEntity(domain: Dependency): DependencyEntity = DependencyEntity(
        id = domain.id,
        sourceTaskId = domain.sourceTaskId,
        targetTaskId = domain.targetTaskId
    )

    fun toDomainList(entities: List<DependencyEntity>): List<Dependency> =
        entities.map { toDomain(it) }
}