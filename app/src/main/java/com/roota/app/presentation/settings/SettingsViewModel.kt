package com.roota.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    fun clearAllData() {
        viewModelScope.launch {
            // TODO: Очистка Room базы данных
            println("Все данные очищены (заглушка)")
        }
    }
}

data class SettingsState(
    val isDarkTheme: Boolean = true,
    val language: String = "ru"
)