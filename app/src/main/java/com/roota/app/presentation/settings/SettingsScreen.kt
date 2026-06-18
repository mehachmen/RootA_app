package com.roota.app.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.roota.app.R
import com.roota.app.presentation.ui.components.RootATopBar
import com.roota.app.presentation.ui.components.SettingsSection
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.ButtonShape
import com.roota.app.presentation.ui.theme.DarkBackground
import com.roota.app.presentation.ui.theme.Dimens
import com.roota.app.presentation.ui.theme.TextPrimary
import com.roota.app.presentation.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    showBackButton: Boolean = true,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RootATopBar(
                title = stringResource(R.string.settings_title),
                onBackClick = if (showBackButton) onBackClick else null
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(padding)
                .padding(Dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.sectionSpacing)
        ) {
            SettingsSection(title = stringResource(R.string.settings_appearance)) {
                ListItem(
                    headlineContent = {
                        Text(stringResource(R.string.settings_theme), color = TextPrimary)
                    },
                    supportingContent = {
                        Text(stringResource(R.string.settings_theme_dark), color = TextSecondary)
                    },
                    trailingContent = {
                        Switch(
                            checked = state.isDarkTheme,
                            onCheckedChange = { },
                            enabled = false,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = AccentGreen,
                                checkedThumbColor = DarkBackground
                            )
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                )
            }

            SettingsSection(title = stringResource(R.string.settings_language_section)) {
                ListItem(
                    headlineContent = {
                        Text(stringResource(R.string.settings_language), color = TextPrimary)
                    },
                    supportingContent = {
                        Text(stringResource(R.string.settings_language_ru), color = TextSecondary)
                    },
                    colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                )
            }

            SettingsSection(title = stringResource(R.string.settings_storage_section)) {
                Column(modifier = Modifier.padding(Dimens.cardPadding)) {
                    Text(
                        text = stringResource(R.string.settings_storage_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.settings_storage_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            SettingsSection(title = stringResource(R.string.settings_about_section)) {
                Column(modifier = Modifier.padding(Dimens.cardPadding)) {
                    Text(
                        text = stringResource(R.string.settings_about_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.settings_about_version),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = stringResource(R.string.settings_about_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = stringResource(R.string.settings_about_copyright),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))

            Button(
                onClick = { showClearDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.buttonHeight),
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = stringResource(R.string.settings_clear_data),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.settings_clear_data)) },
            text = { Text(stringResource(R.string.settings_clear_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        viewModel.clearAllData {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = state.clearMessage ?: ""
                                )
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.settings_clear_data), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = com.roota.app.presentation.ui.theme.DarkSurface
        )
    }
}
