package com.roota.app.presentation.project_create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.roota.app.R
import com.roota.app.presentation.ui.components.ColorTagPicker
import com.roota.app.presentation.ui.components.LinkColorPicker
import com.roota.app.presentation.ui.components.ProjectPreviewCard
import com.roota.app.presentation.ui.components.RootATextField
import com.roota.app.presentation.ui.components.RootATopBar
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.ButtonShape
import com.roota.app.presentation.ui.theme.DarkBackground
import com.roota.app.presentation.ui.theme.Dimens
import com.roota.app.presentation.ui.theme.TextSecondary

@Composable
fun CreateProjectScreen(
    onProjectCreated: (Long) -> Unit,
    onCancel: () -> Unit,
    viewModel: CreateProjectViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RootATopBar(
                title = stringResource(R.string.create_project_screen_title),
                onBackClick = onCancel
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(padding)
                .padding(Dimens.screenPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.create_project_name_section),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            RootATextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                label = stringResource(R.string.create_project_name_hint),
                isError = state.titleError != null,
                supportingText = state.titleError
            )

            Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

            Text(
                text = stringResource(R.string.create_project_desc_section),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            RootATextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = stringResource(R.string.create_project_desc_hint),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

            ColorTagPicker(
                selectedArgb = state.colorTagArgb,
                onColorSelected = viewModel::onColorTagChange
            )

            Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

            ProjectPreviewCard(
                title = state.title,
                description = state.description,
                colorTagArgb = state.colorTagArgb
            )

            Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

            LinkColorPicker(
                selectedArgb = state.linkColorArgb,
                onColorSelected = viewModel::onLinkColorChange
            )

            Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

            Button(
                onClick = { viewModel.createProject(onCreated = onProjectCreated) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.buttonHeight),
                enabled = !state.isLoading,
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGreen,
                    contentColor = Color.Black,
                    disabledContainerColor = AccentGreen.copy(alpha = 0.5f)
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.create_project_save),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
