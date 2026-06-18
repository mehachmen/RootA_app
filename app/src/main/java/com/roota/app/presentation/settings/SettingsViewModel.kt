package com.roota.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roota.app.domain.usecase.settings.ClearAllDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val clearAllDataUseCase: ClearAllDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    fun clearAllData(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                clearAllDataUseCase()
                _state.value = _state.value.copy(clearMessage = "Данные очищены")
                onComplete()
            } catch (e: Exception) {
                _state.value = _state.value.copy(clearMessage = e.message)
                onComplete()
            }
        }
    }
}

data class SettingsState(
    val isDarkTheme: Boolean = true,
    val language: String = "ru",
    val clearMessage: String? = null
)
