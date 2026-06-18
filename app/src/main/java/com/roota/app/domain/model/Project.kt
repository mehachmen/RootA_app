package com.roota.app.domain.model

data class Project(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)