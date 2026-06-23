package com.roota.app.domain.repository

interface UserSettingsRepository {
    suspend fun isOnboardingCompleted(): Boolean
    suspend fun setOnboardingCompleted(completed: Boolean)
}
