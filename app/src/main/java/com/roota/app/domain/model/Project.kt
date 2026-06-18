package com.roota.app.domain.model

data class Project(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val colorTagArgb: Long = 0xFF39FF14L,
    val linkColorArgb: Long = 0xFF448AFFFFL,
    val graphScale: Float = 1f,
    val graphOffsetX: Float = 0f,
    val graphOffsetY: Float = 0f,
    val createdAt: Long = System.currentTimeMillis()
)
