package com.roota.app.data.repo

import com.roota.app.data.local.dao.UserSettingsDao
import com.roota.app.data.local.entity.UserSettingsEntity
import com.roota.app.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    private val userSettingsDao: UserSettingsDao
) : UserSettingsRepository {

    override suspend fun isOnboardingCompleted(): Boolean {
        return userSettingsDao.getSettings().first()?.onboardingCompleted ?: false
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        val current = userSettingsDao.getSettings().first()
        val settings = current?.copy(onboardingCompleted = completed)
            ?: UserSettingsEntity(onboardingCompleted = completed)
        userSettingsDao.insertSettings(settings)
    }
}
