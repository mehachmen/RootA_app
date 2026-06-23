package com.roota.app.domain.usecase.settings

import com.roota.app.data.local.AppDatabase
import com.roota.app.data.local.entity.UserSettingsEntity
import com.roota.app.data.local.dao.UserSettingsDao
import javax.inject.Inject

class ClearAllDataUseCase @Inject constructor(
    private val database: AppDatabase,
    private val userSettingsDao: UserSettingsDao
) {
    suspend operator fun invoke() {
        database.clearAllTables()
        userSettingsDao.insertSettings(UserSettingsEntity(onboardingCompleted = false))
    }
}
