package com.roota.app.domain.model

data class Dependency(
    val id: Long = 0,
    val sourceTaskId: Long,
    val targetTaskId: Long
)