package com.roota.app.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Темная тема (фиксирована по дизайну)
            Card {
                ListItem(
                    headlineContent = { Text("Тема") },
                    supportingContent = { Text("Тёмная тема (фиксирована)") },
                    trailingContent = {
                        Switch(
                            checked = true,
                            onCheckedChange = { /* Пока отключено */ }
                        )
                    }
                )
            }

            // Язык
            Card {
                ListItem(
                    headlineContent = { Text("Язык") },
                    supportingContent = { Text("Русский") }
                )
            }

            // Локальное хранилище
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Локальное хранилище", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Все данные сохраняются только на устройстве.")
                    Text("Облачная синхронизация будет в следующих версиях.")
                }
            }

            // О приложении
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("О приложении RootA", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Версия 0.1 (MVP)")
                    Text("Графовая система задач")
                    Text("© 2026")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Кнопка очистки данных (для разработки)
            OutlinedButton(
                onClick = { viewModel.clearAllData() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Очистить все данные (для теста)")
            }
        }
    }
}