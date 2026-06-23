package com.roota.app.domain.usecase.settings

import com.roota.app.domain.repository.UserSettingsRepository
import javax.inject.Inject

class IsOnboardingCompletedUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) {
    suspend operator fun invoke(): Boolean {
        return userSettingsRepository.isOnboardingCompleted()
    }
}
